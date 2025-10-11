package leo.lija.chess;

import leo.lija.chess.utils.Pair;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static leo.lija.chess.Color.WHITE;
import static leo.lija.chess.Role.BISHOP;
import static leo.lija.chess.Role.KING;
import static leo.lija.chess.Role.KNIGHT;
import static leo.lija.chess.Role.QUEEN;
import static leo.lija.chess.Role.ROOK;
import static leo.lija.chess.Side.KING_SIDE;
import static leo.lija.chess.Side.QUEEN_SIDE;

public class Actor {

	@Getter
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

	private Optional<List<Move>> cachedMoves = Optional.empty();

	List<Move> moves() {
		if (cachedMoves.isPresent()) return cachedMoves.get();

		Role role = piece.role();
		List<Move> movesWithoutSafety = switch(role) {
			case BISHOP -> longRange(BISHOP.dirs);
			case QUEEN -> longRange(QUEEN.dirs);
			case KNIGHT -> shortRange(KNIGHT.dirs);
			case KING -> Stream.concat(preventsCastle(shortRange(KING.dirs)).stream(), castle().stream()).toList();
			case ROOK -> {
				Color color = color();
				yield board.kingPosOf(color)
					.flatMap(kingPos -> Side.kingRookSide(kingPos, pos))
					.filter(side -> board.getHistory().canCastle(color, side))
					.map(side -> {
						History h = board.getHistory().withoutCastle(color, side);
						return longRange(ROOK.dirs).stream()
							.map(m -> m.withHistory(h))
							.toList();
					}).orElse(longRange(ROOK.dirs));
			}
			case PAWN -> pawn();
		};

		List<Move> moves = kingSafety(movesWithoutSafety);
		cachedMoves = Optional.of(moves);
		return moves;
	}

	public List<Pos> destinations() {
		return moves().stream().map(Move::dest).toList();
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

		return switch (role) {
			case PAWN -> pawnDir.apply(pos)
				.map(next -> Stream.of(next.left(), next.right())
					.filter(Optional::isPresent)
					.map(Optional::get)
					.collect(Collectors.toSet()))
				.orElse(Set.of());
			case QUEEN, BISHOP, ROOK -> Set.copyOf(longRangePoss(role.dirs));
			default -> role.dirs.stream()
				.map(dir -> dir.apply(pos))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toSet());
		};
	}

	public String hash() {
		return piece.fen() + pos.key();
	}

	private List<Move> kingSafety(List<Move> ms) {
		return ms.stream()
			.filter(m -> m.after().actorsOf(color().getOpposite()).stream()
				.noneMatch(a -> m.after().kingPosOf(color()).map(a::threatens).orElse(false)))
			.toList();
	}

	Set<Pos> enemyThreats = null;

	private List<Move> castle() {
		return Stream.of(castleOn(KING_SIDE),	castleOn(QUEEN_SIDE)).filter(Optional::isPresent).map(Optional::get).toList();
	}

	Optional<Move> castleOn(Side side) {
		Color color = color();
		return board.kingPosOf(color)
			.filter(p -> board.getHistory().canCastle(color, side))
			.flatMap(kingPos -> {
				List<Pos> tripToRook = side.tripToRook.apply(kingPos, board);
				if (tripToRook.isEmpty()) return Optional.empty();
				Pos rookPos = tripToRook.getLast();
				if (board.at(rookPos).isEmpty() || !board.at(rookPos).get().equals(color.rook())) return Optional.empty();
				Optional<Pos> newKingPos = Pos.posAt(side.castledKingX, kingPos.getY());
				Optional<Pos> newRookPos = Pos.posAt(side.castledRookX, rookPos.getY());
				if (newKingPos.isEmpty() || newRookPos.isEmpty()) return Optional.empty();

				List<Pos> securedPoss = kingPos.horizontalPath(newKingPos.get());
				if (enemyThreats == null) {
					enemyThreats = board.actorsOf(color().getOpposite()).stream()
						.flatMap(actor -> actor.threats().stream())
						.collect(Collectors.toSet());
				}
				if (!Collections.disjoint(securedPoss, enemyThreats)) return Optional.empty();

				return board.take(rookPos)
					.map(b1 -> b1.move(kingPos, newKingPos.get())
						.map(b -> Pair.of(b, newKingPos.get()))
						.orElse(Pair.of(b1, rookPos)))
					.flatMap(b2AndTarget -> {
						Board b2 = b2AndTarget.getFirst();
						Pos target = b2AndTarget.getSecond();
						return b2.place(color.rook(), newRookPos.get())
							.map(b -> b.updateHistory(b1 -> b1.withoutCastles(color)))
							.map(b -> moveCastle(target, b, Optional.of(Pair.of(Pair.of(kingPos, newKingPos.get()), Pair.of(rookPos, newRookPos.get())))));
					});
			});
	}

	private List<Move> preventsCastle(List<Move> ms) {
		if (history().canCastle(color())) {
			History newHistory = history().withoutCastles(color());
			return ms.stream()
				.map(m -> m.withHistory(newHistory))
				.toList();
		}
		return ms;
	}

	private List<Move> shortRange(List<Function<Pos, Optional<Pos>>> dirs) {
		List<Pos> tos = dirs.stream()
			.map(dir -> dir.apply(pos))
			.filter(Optional::isPresent).map(Optional::get)
			.collect(Collectors.toList());
		tos.removeAll(friends());
		return tos.stream().map(to -> enemies().contains(to)
			? move(to, board.taking(pos, to).get(), Optional.of(to))
			: move(to, board.moveTo(pos, to)))
			.toList();
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

	private List<Move> longRange(List<Function<Pos, Optional<Pos>>> dirs) {
		return dirs.stream().flatMap(dir -> forwardImplications(pos, dir).stream()).toList();
	}

	private List<Move> forwardImplications(Pos p, Function<Pos, Optional<Pos>> dir) {
		List<Move> res = new ArrayList<>();
		Optional<Pos> optNext = dir.apply(p);
		while (optNext.isPresent() && !friends().contains(optNext.get())) {
			Pos next = optNext.get();
			if (enemies().contains(next)) {
				res.add(move(next, board.taking(pos, next).get(), Optional.of(pos))); // mistake?
				break;
			}
			res.add(move(next, board.moveTo(pos, next)));
			optNext = dir.apply(optNext.get());
		}
		return res;
	}

	private List<Move> pawn() {
		return pawnDir.apply(pos).map(next -> {
			Optional<Pos> fwd = Optional.of(next).filter(p -> !board.occupations().contains(p));

			List<Optional<Move>> optPositions = List.of(
				fwd.flatMap(p -> forward(p)),
				fwd.filter((p) -> pos.getY() == color().getUnmovedPawnY())
				.flatMap(p -> pawnDir.apply(p).filter(p2 -> !board.occupations().contains(p2))
						.flatMap(p2 -> board.move(pos, p2).map(b -> move(p2, b)))),
				capture(Pos::left, next),
				capture(Pos::right, next),
				enpassant(pawnDir, next, Pos::left),
				enpassant(pawnDir, next, Pos::right)
			);
			return optPositions.stream().filter(Optional::isPresent).map(Optional::get).toList();
		}).orElse(List.of());
	}

	private Optional<Move> forward(Pos p) {
		if (pos.getY() == color().getPromotablePawnY()) return board.promote(pos, p).map(b -> movePromote(p, b, Optional.of(QUEEN)));
		return board.move(pos, p).map(b -> move(p, b));
	}

	private Optional<Move> capture(Function<Pos, Optional<Pos>> horizontal, Pos next) {
		Optional<Pos> optPos = horizontal.apply(next).filter(enemies()::contains);
		Optional<Board> optBoard = optPos.flatMap(p -> board.taking(pos, p));
		return optBoard.map(b -> move(optPos.get(), b, optPos));
	}

	private Optional<Move> enpassant(Function<Pos, Optional<Pos>> dir, Pos next, Function<Pos, Optional<Pos>> horizontal) {
		if (pos.getY() != color().getPassablePawnY()) return Optional.empty();
		Optional<Pos> optVictimPos = horizontal.apply(pos);
		Optional<Piece> optVictim = optVictimPos.flatMap(board::at).filter(p -> p.equals(color().getOpposite().pawn()));
		return optVictim.flatMap(victim -> {
			Optional<Pos> optTargetPos = horizontal.apply(next);
			Optional<Pos> optVictimFrom = optVictimPos.flatMap(dir).flatMap(dir);
			if (!board.getHistory().lastMove().equals(Optional.of(Pair.of(optVictimFrom.get(), optVictimPos.get())))) return Optional.empty();
			Board b = board.taking(pos, optTargetPos.get(), optVictimPos).get();
			return Optional.of(moveEnPassant(optTargetPos.get(), b));
		});
	}

	private Move move(Pos dest, Board after) {
		return move(dest, after, Optional.empty());
	}

	private Move move(Pos dest, Board after, Optional<Pos> capture) {
		return move(dest, after, capture, Optional.empty(), Optional.empty(), false);
	}

	private Move moveEnPassant(Pos dest, Board after) {
		return move(dest, after, Optional.empty(), Optional.empty(), Optional.empty(), true);
	}

	private Move moveCastle(Pos dest, Board after, Optional<Pair<Pair<Pos, Pos>, Pair<Pos, Pos>>> castle) {
		return move(dest, after, Optional.empty(), castle, Optional.empty(), false);
	}

	private Move movePromote(Pos dest, Board after, Optional<Role> promotion) {
		return move(dest, after, Optional.empty(), Optional.empty(), promotion, false);
	}

	private Move move(Pos dest, Board after, Optional<Pos> capture, Optional<Pair<Pair<Pos, Pos>, Pair<Pos, Pos>>> castle, Optional<Role> promotion, boolean enpassant) {
		return new Move(piece, pos, dest, board, after, capture, promotion, castle, enpassant);
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
