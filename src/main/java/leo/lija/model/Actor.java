package leo.lija.model;

import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static leo.lija.model.Color.BLACK;
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
		Set<Pos> tos = tos();
		Map<Pos, Board> implicationsWithoutSafety;
		if (piece.is(PAWN)) {
			implicationsWithoutSafety = tos.stream().collect(Collectors.toMap(Function.identity(), to -> board));
		} else {
			implicationsWithoutSafety = tos.stream().collect(Collectors.toMap(Function.identity(), to -> enemies().contains(to)
				? board.take(to).moveTo(pos, to)
				: board.moveTo(pos, to)));
		}
		return kingSafety(implicationsWithoutSafety);
	}

	private Set<Pos> tos() {
		Color color = color();
		Role role = piece.role();

		if (role == Role.PAWN) {
			return pawnMoves();
		}
		else if (role.trajectory) {
			return trajectories(role.dirs);
		} else {
			Set<Pos> allPoss = role.dirs.stream()
				.map(dir -> dir.apply(pos))
				.filter(Optional::isPresent).map(Optional::get)
				.collect(Collectors.toSet());
			allPoss.removeAll(friends());
			return allPoss;
		}
	}

	private Set<Pos> pawnMoves() {
		Color color = color();
		Set<Pos> enemies = board.occupation().get(color.getOpposite());
		Function<Pos, Optional<Pos>> dir = color == Color.WHITE ? Pos::up : Pos::down;
		return dir.apply(pos).map(next -> {
				boolean notMoved = (color == WHITE && pos.getY() == 2) || pos.getY() == 7;
				boolean passable = (color == WHITE && pos.getY() == 5) || pos.getY() == 4;
				Optional<Pos> one = Optional.of(next).filter(p -> !board.occupations().contains(p));

				List<Optional<Pos>> optPositions = List.of(
					one,
					notMoved ? one.flatMap(o -> dir.apply(o).filter(p -> !board.occupations().contains(p))) : Optional.empty(),
					next.left().filter(enemies::contains),
					next.right().filter(enemies::contains),
					passable ? enpassant(dir, next, Pos::left) : Optional.empty(),
					passable ? enpassant(dir, next, Pos::right) : Optional.empty()
				);
				return optPositions.stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet());
			}
		).orElse(Set.of());
	}

	private Optional<Pos> enpassant(Function<Pos, Optional<Pos>> dir, Pos next, Function<Pos, Optional<Pos>> horizontal) {
		Optional<Pos> optVictimPos = horizontal.apply(pos);
		Optional<Piece> optVictim = optVictimPos.flatMap(board::at);
		if (optVictim.isEmpty()) return Optional.empty();
		Piece victim = optVictim.get();
		if (!victim.equals(color().getOpposite().pawn())) return Optional.empty();
		Optional<Pos> optTargetPos = horizontal.apply(next);
		Optional<Pos> optVictimFrom = dir.apply(optVictimPos.get()).flatMap(dir);
		return board.getHistory().lastMove().equals(Optional.of(Pair.of(optVictimFrom.get(), optVictimPos.get())))
			? optTargetPos
			: Optional.empty();
	}

	private Map<Pos, Board> kingSafety(Map<Pos, Board> implications) {
		return implications.entrySet().stream()
			.filter(e -> e.getValue().actorsOf(color().getOpposite()).stream()
				.noneMatch(a -> e.getValue().kingPosOf(color()).map(a::threatens).orElse(false)))
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	public boolean threatens(Pos to) {
		if (!enemies().contains(to)) return false;

		Role role = piece.role();
		Set<Pos> positions;

		if (role == Role.PAWN) {
			positions = dir().apply(pos)
				.map(next -> Set.of(next.left(), next.right()).stream()
					.filter(Optional::isPresent)
					.map(Optional::get)
					.collect(Collectors.toSet()))
				.orElse(Set.of());
		} else if (role.trajectory) {
			positions = new Trajectory(role.dirs, friends(), enemies()).from(pos);
		} else if (role.threatens) {
			positions = role.dirs.stream()
				.map(dir -> dir.apply(pos))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toSet());
		} else {
			positions = Set.of();
		}

		return positions.contains(to);
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

	private Set<Pos> trajectories(List<Function<Pos, Optional<Pos>>> dirs) {
		return dirs.stream().flatMap(dir -> forward(pos, dir).stream()).collect(Collectors.toSet());
	}

	private List<Pos> forward(Pos p, Function<Pos, Optional<Pos>> dir) {
		List<Pos> res = new ArrayList<>();
		Optional<Pos> next = dir.apply(p);
		while (next.isPresent() && !friends().contains(next.get())) {
			res.add(next.get());
			if (enemies().contains(next.get())) break;
			next = dir.apply(next.get());
		}
		return res;
	}
}
