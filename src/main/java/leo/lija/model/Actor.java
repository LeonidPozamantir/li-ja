package leo.lija.model;

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
import static leo.lija.model.Role.PAWN;

@RequiredArgsConstructor
public class Actor {

	private final Piece piece;
	private final Pos pos;
	private final Board board;

	public Set<Pos> moves() {
		return implications().keySet();
	}

	private Map<Pos, Board> implications() {
		Map<Pos, Board> implicationsWithoutSafety;
		Color color = color();
		Role role = piece.role();
		if (piece.is(PAWN)) {
			Function<Pos, Optional<Pos>> dir = color == Color.WHITE ? Pos::up : Pos::down;
			implicationsWithoutSafety = dir.apply(pos).map(next -> {
					boolean notMoved = (color == WHITE && pos.getY() == 2) || pos.getY() == 7;
					Optional<Pos> one = Optional.of(next).filter(p -> !board.occupations().contains(p));

					List<Optional<Pair<Pos, Board>>> optPositions = List.of(
						one.flatMap(p -> board.moveToOption(pos, p).map(b -> Pair.of(p, b))),
						one.filter((p) -> notMoved)
							.flatMap(p -> dir.apply(p).filter(p2 -> !board.occupations().contains(p2))
								.flatMap(p2 -> board.moveToOption(pos, p2).map(b -> Pair.of(p2, b)))),
						capture(Pos::left, next),
						capture(Pos::right, next),
						enpassant(dir, next, Pos::left),
						enpassant(dir, next, Pos::right)
					);
					return optPositions.stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
				}
			).orElse(Map.of());
		} else if (role.trajectory) {
			implicationsWithoutSafety = implicationTrajectories(role.dirs, pos);
		}
		else {
			Set<Pos> tos = role.dirs.stream()
				.map(dir -> dir.apply(pos))
				.filter(Optional::isPresent).map(Optional::get)
				.collect(Collectors.toSet());
			tos.removeAll(friends());
			implicationsWithoutSafety = tos.stream().collect(Collectors.toMap(Function.identity(), to -> enemies().contains(to)
				? board.take(to).moveTo(pos, to)
				: board.moveTo(pos, to)));
		}

		return kingSafety(implicationsWithoutSafety);
	}

	private Optional<Pair<Pos, Board>> capture(Function<Pos, Optional<Pos>> horizontal, Pos next) {
		Optional<Pos> optPos = horizontal.apply(next).filter(enemies()::contains);
		Optional<Board> optBoard = optPos.map(p -> board.take(p).moveTo(pos, p));
		return optBoard.map(b -> Pair.of(optPos.get(), b));
	}

	private Optional<Pair<Pos, Board>> enpassant(Function<Pos, Optional<Pos>> dir, Pos next, Function<Pos, Optional<Pos>> horizontal) {
		boolean passable = (color() == WHITE && pos.getY() == 5) || pos.getY() == 4;
		if (!passable) return Optional.empty();
		Optional<Pos> optVictimPos = horizontal.apply(pos);
		Optional<Piece> optVictim = optVictimPos.flatMap(board::at).filter(piece -> piece.equals(color().getOpposite().pawn()));
		return optVictim.flatMap(victim -> {
			Optional<Pos> optTargetPos = horizontal.apply(next);
			Optional<Pos> optVictimFrom = optVictimPos.flatMap(dir).flatMap(dir);
			if (!board.getHistory().lastMove().equals(Optional.of(Pair.of(optVictimFrom.get(), optVictimPos.get())))) return Optional.empty();
			Board b1 = board.moveToOption(pos, optTargetPos.get()).get();
			Board b2 = b1.take(optVictimPos.get());
			return Optional.of(Pair.of(optTargetPos.get(), b2));
		});
	}

	private Map<Pos, Board> kingSafety(Map<Pos, Board> implications) {
		return implications.entrySet().stream()
			.filter(e -> e.getValue().actorsOf(color().getOpposite()).stream()
				.noneMatch(a -> e.getValue().kingPosOf(color()).map(a::threatens).orElse(false)))
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	boolean threatens(Pos to) {
		return threats().contains(to) && enemies().contains(to);
	}

	Set<Pos> threats() {
		Role role = piece.role();

		if (role == Role.PAWN) {
			return dir().apply(pos)
				.map(next -> Set.of(next.left(), next.right()).stream()
					.filter(Optional::isPresent)
					.map(Optional::get)
					.collect(Collectors.toSet()))
				.orElse(Set.of());
		} else if (role.trajectory) {
			return posTrajectories(role.dirs);
		} else if (role.threatens) {
			return role.dirs.stream()
				.map(dir -> dir.apply(pos))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toSet());
		} else {
			return Set.of();
		}
	}

	Color color() {
		return piece.color();
	}
	boolean is(Color color) {
		return piece.is(color);
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

	private Set<Pos> posTrajectories(List<Function<Pos, Optional<Pos>>> dirs) {
		return dirs.stream().flatMap(dir -> forwardPos(pos, dir).stream()).collect(Collectors.toSet());
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

	private Map<Pos, Board> implicationTrajectories(List<Function<Pos, Optional<Pos>>> dirs, Pos from) {
		return dirs.stream().flatMap(dir -> forwardImplications(from, dir).stream()).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
	}

	private List<Pair<Pos, Board>> forwardImplications(Pos p, Function<Pos, Optional<Pos>> dir) {
		List<Pair<Pos, Board>> res = new ArrayList<>();
		Optional<Pos> optNext = dir.apply(p);
		while (optNext.isPresent() && !friends().contains(optNext.get())) {
			Pos next = optNext.get();
			if (enemies().contains(next)) {
				res.add(Pair.of(next, board.take(next).moveTo(p, next)));
				break;
			}
			res.add(Pair.of(next, board.moveTo(p, next)));
			optNext = dir.apply(optNext.get());
		}
		return res;
	}
}
