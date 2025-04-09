package leo.lija.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static leo.lija.model.Color.WHITE;
import static leo.lija.model.Role.KING;
import static leo.lija.model.Role.PAWN;

@RequiredArgsConstructor
public class Actor {

	private final Piece piece;
	@Getter
	private final Pos pos;
	private final Board board;

	private Optional<Map<Pos, Board>> cachedImplications = Optional.empty();

	Map<Pos, Board> implications() {
		if (cachedImplications.isPresent()) return cachedImplications.get();

		Map<Pos, Board> implicationsWithoutSafety;
		Role role = piece.role();
		if (piece.is(PAWN)) {
			implicationsWithoutSafety = pawn();
		} else if (role == KING) {
			implicationsWithoutSafety = shortRange(KING.dirs);
			implicationsWithoutSafety.putAll(castle());
		} else if (role.longRange) {
			implicationsWithoutSafety = longRange(role.dirs);
		}
		else {
			implicationsWithoutSafety = shortRange(role.dirs);
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
	Set<Pos> friends() {
		return board.occupation().get(color());
	}
	Set<Pos> enemies() {
		return board.occupation().get(color().getOpposite());
	}
	Function<Pos, Optional<Pos>> dir() {
		return color() == WHITE ? Pos::up : Pos::down;
	}

	boolean threatens(Pos to) {
		Role role = piece.role();
		List<Pos> threats;

		if (role == Role.PAWN) {
			threats = dir().apply(pos)
				.map(next -> List.of(next.left(), next.right()).stream()
					.filter(Optional::isPresent)
					.map(Optional::get)
					.toList())
				.orElse(List.of());
		} else if (role.longRange) {
			threats = longRangePoss(role.dirs);
		} else {
			threats = role.dirs.stream()
				.map(dir -> dir.apply(pos))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.toList();
		}

		return threats.contains(to) && enemies().contains(to);
	}

	private Map<Pos, Board> kingSafety(Map<Pos, Board> implications) {
		return implications.entrySet().stream()
			.filter(e -> e.getValue().actorsOf(color().getOpposite()).stream()
				.noneMatch(a -> e.getValue().kingPosOf(color()).map(a::threatens).orElse(false)))
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	private Map<Pos, Board> castle() {
		Color color = color();
		Optional<Pair<Pos, Board>> kingSide = board.kingPosOf(color)
			.filter(p -> board.getHistory().canCastleKingSide(color))
			.flatMap(kingPos -> {
				Piece rook = color.rook();
				Optional<Pos> rookPos = board.actorsOf(color).stream()
					.filter(a -> a.is(rook) && a.getPos().getX() > kingPos.getX())
					.findFirst()
					.map(Actor::getPos);
				Optional<Pos> newKingPos = kingPos.shiftRight(2);
				Optional<Pos> newRookPos = newKingPos.flatMap(Pos::left);
				if (rookPos.isEmpty() || newKingPos.isEmpty() || newRookPos.isEmpty()) return Optional.empty();
				Optional<Board> newBoard = board.take(rookPos.get())
					.flatMap(b -> b.move(kingPos, newKingPos.get()))
					.flatMap(b -> b.placeAtOpt(rook, newRookPos.get()));
				return newBoard.map(b -> Pair.of(newKingPos.get(), b));
			});
		return kingSide.map(p -> Map.of(p.getFirst(), p.getSecond()))
			.orElse(Map.of());
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
		Function<Pos, Optional<Pos>> dir = color() == Color.WHITE ? Pos::up : Pos::down;
		return dir.apply(pos).map(next -> {
				boolean notMoved = (color() == WHITE && pos.getY() == 2) || pos.getY() == 7;
				Optional<Pos> one = Optional.of(next).filter(p -> !board.occupations().contains(p));

				List<Optional<Pair<Pos, Board>>> optPositions = List.of(
					one.flatMap(p -> board.move(pos, p).map(b -> Pair.of(p, b))),
					one.filter((p) -> notMoved)
						.flatMap(p -> dir.apply(p).filter(p2 -> !board.occupations().contains(p2))
							.flatMap(p2 -> board.move(pos, p2).map(b -> Pair.of(p2, b)))),
					capture(Pos::left, next),
					capture(Pos::right, next),
					enpassant(dir, next, Pos::left),
					enpassant(dir, next, Pos::right)
				);
				return optPositions.stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
			}
		).orElse(Map.of());
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
}
