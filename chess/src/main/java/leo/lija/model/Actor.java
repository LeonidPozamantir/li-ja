package leo.lija.model;

import leo.lija.utils.Pair;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static leo.lija.model.Color.WHITE;
import static leo.lija.model.Role.BISHOP;
import static leo.lija.model.Role.KING;
import static leo.lija.model.Role.KNIGHT;
import static leo.lija.model.Role.PAWN;
import static leo.lija.model.Role.QUEEN;
import static leo.lija.model.Role.ROOK;
import static leo.lija.model.Side.KING_SIDE;
import static leo.lija.model.Side.QUEEN_SIDE;

public class Actor {

	private final Piece piece;
	@Getter
	private final Pos pos;
	private final Board board;

	public Actor(Piece piece, Pos pos, Board board) {
		this.piece = piece;
		this.pos = pos;
		this.board = board;
		this.pawnDir = color() == WHITE ? Pos::up : Pos::down;
	}

	private Optional<Map<Pos, Board>> cachedImplications = Optional.empty();

	Map<Pos, Board> implications() {
		if (cachedImplications.isPresent()) return cachedImplications.get();

		Map<Pos, Board> implicationsWithoutSafety = Map.of();
		Role role = piece.role();
		if (piece.is(BISHOP)) {
			implicationsWithoutSafety = longRange(BISHOP.dirs);
		} else if (piece.is(QUEEN)) {
			implicationsWithoutSafety = longRange(QUEEN.dirs);
		} else if (piece.is(KNIGHT)) {
			implicationsWithoutSafety = shortRange(KNIGHT.dirs);
		} else if (role == KING) {
			implicationsWithoutSafety = preventsCastle(shortRange(KING.dirs));
			implicationsWithoutSafety.putAll(castle());
		} else if (role == ROOK) {
			Color color = color();
			implicationsWithoutSafety = board.kingPosOf(color)
				.flatMap(kingPos -> Side.kingRookSide(kingPos, pos))
				.filter(side -> board.getHistory().canCastle(color, side))
				.map(side -> {
					History h = board.getHistory().withoutCastle(color, side);
					return longRange(ROOK.dirs).entrySet().stream()
						.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().withHistory(h)));
				}).orElse(longRange(ROOK.dirs));
		} else if (piece.is(PAWN)) {
			implicationsWithoutSafety = pawn();
		}

		Map<Pos, Board> implications = kingSafety(implicationsWithoutSafety);
		cachedImplications = Optional.of(implications);
		return implications;
	}

	public Set<Pos> moves() {
		return implications().keySet();
	}

	Color color() {
		return piece.color();
	}
	boolean is(Color color) {
		return piece.is(color);
	}
	boolean is(Piece piece) {
		return this.piece.equals(piece);
	}


	boolean threatens(Pos to) {
		return threats().contains(to) && enemies().contains(to);
	}

	Set<Pos> threats() {
		Role role = piece.role();

		if (role == PAWN) {
			return pawnDir.apply(pos)
				.map(next -> List.of(next.left(), next.right()).stream()
					.filter(Optional::isPresent)
					.map(Optional::get)
					.collect(Collectors.toSet()))
				.orElse(Set.of());
		} else if (role.longRange) {
			return Set.copyOf(longRangePoss(role.dirs));
		} else {
			return role.dirs.stream()
				.map(dir -> dir.apply(pos))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toSet());
		}
	}

	private Map<Pos, Board> kingSafety(Map<Pos, Board> implications) {
		return implications.entrySet().stream()
			.filter(e -> e.getValue().actorsOf(color().getOpposite()).stream()
				.noneMatch(a -> e.getValue().kingPosOf(color()).map(a::threatens).orElse(false)))
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	Set<Pos> enemyThreats = null;

	private Map<Pos, Board> castle() {
		Map<Pos, Board> res = new HashMap<>();
		castleOn(KING_SIDE).ifPresent(pair -> res.put(pair.getFirst(), pair.getSecond()));
		castleOn(QUEEN_SIDE).ifPresent(pair -> res.put(pair.getFirst(), pair.getSecond()));
		return res;
	}

	Optional<Pair<Pos, Board>> castleOn(Side side) {
		Color color = color();
		return board.kingPosOf(color)
			.filter(p -> board.getHistory().canCastle(color, side))
			.flatMap(kingPos -> {
				List<Pos> tripToRook = side.tripToRook.apply(kingPos, board);
				if (tripToRook.isEmpty()) return Optional.empty();
				Pos rookPos = tripToRook.getLast();
				if (board.at(rookPos).isEmpty() || !board.at(rookPos).get().equals(color.rook())) return Optional.empty();
				Optional<Pos> newKingPos = Pos.makePos(side.castledKingX, kingPos.getY());
				Optional<Pos> newRookPos = Pos.makePos(side.castledRookX, rookPos.getY());
				if (newKingPos.isEmpty() || newRookPos.isEmpty()) return Optional.empty();

				List<Pos> securedPoss = kingPos.horizontalPath(newKingPos.get());
				if (enemyThreats == null) {
					enemyThreats = board.actorsOf(color().getOpposite()).stream()
						.flatMap(actor -> actor.threats().stream())
						.collect(Collectors.toSet());
				}
				if (!Collections.disjoint(securedPoss, enemyThreats)) return Optional.empty();

				Optional<Board> newBoard = board.take(rookPos)
					.flatMap(b -> b.move(kingPos, newKingPos.get()))
					.flatMap(b -> b.placeAtOpt(color.rook(), newRookPos.get()));
				return newBoard.map(b -> Pair.of(newKingPos.get(), b.updateHistory(b1 -> b1.withoutCastles(color))));
			});
	}

	private Map<Pos, Board> preventsCastle(Map<Pos, Board> implications) {
		if (history().canCastle(color())) {
			History newHistory = history().withoutCastles(color());
			return implications.entrySet().stream()
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().withHistory(newHistory)));
		}
		return implications;
	}

	private Map<Pos, Board> shortRange(List<Function<Pos, Optional<Pos>>> dirs) {
		Set<Pos> tos = dirs.stream()
			.map(dir -> dir.apply(pos))
			.filter(Optional::isPresent).map(Optional::get)
			.collect(Collectors.toSet());
		tos.removeAll(friends());
		return tos.stream().collect(Collectors.toMap(Function.identity(), to -> enemies().contains(to)
			? board.taking(pos, to).get()
			: board.moveTo(pos, to)));
	}

	private List<Pos> longRangePoss(List<Function<Pos, Optional<Pos>>> dirs) {
		return dirs.stream().flatMap(dir -> forwardPos(pos, dir).stream()).toList();
	}

	private List<Pos> forwardPos(Pos p, Function<Pos, Optional<Pos>> dir) {
		List<Pos> res = new ArrayList<>();
		Optional<Pos> next = dir.apply(p);
		while (next.isPresent() && !friends().contains(next.get())) {
			res.add(next.get());
			if (enemies().contains(next.get())) break;
			next = dir.apply(next.get());
		}
		return res;
	}

	private Map<Pos, Board> longRange(List<Function<Pos, Optional<Pos>>> dirs) {
		return dirs.stream().flatMap(dir -> forwardImplications(pos, dir).stream()).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
	}

	private List<Pair<Pos, Board>> forwardImplications(Pos p, Function<Pos, Optional<Pos>> dir) {
		List<Pair<Pos, Board>> res = new ArrayList<>();
		Optional<Pos> optNext = dir.apply(p);
		while (optNext.isPresent() && !friends().contains(optNext.get())) {
			Pos next = optNext.get();
			if (enemies().contains(next)) {
				res.add(Pair.of(next, board.taking(p, next).get()));
				break;
			}
			res.add(Pair.of(next, board.moveTo(p, next)));
			optNext = dir.apply(optNext.get());
		}
		return res;
	}

	private Map<Pos, Board> pawn() {
		return pawnDir.apply(pos).map(next -> {
			boolean notMoved = (color() == WHITE && pos.getY() == 2) || pos.getY() == 7;
			Optional<Pos> fwd = Optional.of(next).filter(p -> !board.occupations().contains(p));

			List<Optional<Pair<Pos, Board>>> optPositions = List.of(
				fwd.flatMap(p -> board.move(pos, p).map(b -> Pair.of(p, b))),
				fwd.filter((p) -> notMoved)
					.flatMap(p -> pawnDir.apply(p).filter(p2 -> !board.occupations().contains(p2))
						.flatMap(p2 -> board.move(pos, p2).map(b -> Pair.of(p2, b)))),
				capture(Pos::left, next),
				capture(Pos::right, next),
				enpassant(pawnDir, next, Pos::left),
				enpassant(pawnDir, next, Pos::right)
			);
			return optPositions.stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
		}).orElse(Map.of());
	}

	private Optional<Pair<Pos, Board>> capture(Function<Pos, Optional<Pos>> horizontal, Pos next) {
		Optional<Pos> optPos = horizontal.apply(next).filter(enemies()::contains);
		Optional<Board> optBoard = optPos.flatMap(p -> board.taking(pos, p));
		return optBoard.map(b -> Pair.of(optPos.get(), b));
	}

	private Optional<Pair<Pos, Board>> enpassant(Function<Pos, Optional<Pos>> dir, Pos next, Function<Pos, Optional<Pos>> horizontal) {
		boolean passable = (color() == WHITE && pos.getY() == 5) || pos.getY() == 4;
		if (!passable) return Optional.empty();
		Optional<Pos> optVictimPos = horizontal.apply(pos);
		Optional<Piece> optVictim = optVictimPos.flatMap(board::at).filter(p -> p.equals(color().getOpposite().pawn()));
		return optVictim.flatMap(victim -> {
			Optional<Pos> optTargetPos = horizontal.apply(next);
			Optional<Pos> optVictimFrom = optVictimPos.flatMap(dir).flatMap(dir);
			if (!board.getHistory().lastMove().equals(Optional.of(Pair.of(optVictimFrom.get(), optVictimPos.get())))) return Optional.empty();
			Board b = board.taking(pos, optTargetPos.get(), optVictimPos).get();
			return Optional.of(Pair.of(optTargetPos.get(), b));
		});
	}

	private History history() {
		return board.getHistory();
	}
	private Set<Pos> friends() {
		return board.occupation().get(color());
	}
	private Set<Pos> enemies() {
		return board.occupation().get(color().getOpposite());
	}
	private final Function<Pos, Optional<Pos>> pawnDir;
}
