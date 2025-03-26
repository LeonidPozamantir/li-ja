package leo.lija.model;

import lombok.AllArgsConstructor;
import org.springframework.data.util.Pair;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static leo.lija.model.Color.BLACK;
import static leo.lija.model.Color.WHITE;

@AllArgsConstructor
public class Actor {

	private final Piece piece;
	private final Pos pos;
	private final Board board;
	private final List<Pair<Pos, Pos>> history = List.of();


	public Set<Pos> moves() {
		Color color = color();
		Set<Pos> friends = board.occupation().get(color);
		Set<Pos> enemies = board.occupation().get(color.getOpposite());
		Role role = piece.role();

		if (role == Role.PAWN) {
			Function<Pos, Optional<Pos>> dir = color == Color.WHITE ? Pos::up : Pos::down;
			return dir.apply(pos).map(next -> {
					boolean notMoved = (color == WHITE && pos.getY() == 2) || (color == BLACK && pos.getY() == 7);
					Optional<Pos> one = Optional.of(next).filter(p -> !board.occupations().contains(p));
					List<Optional<Pos>> optPositions = List.of(
						one,
						notMoved ? one.flatMap(o -> dir.apply(o).filter(p -> !board.occupations().contains(p))) : Optional.empty(),
						next.left().filter(enemies::contains),
						next.right().filter(enemies::contains)
					);
					return optPositions.stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet());
				}
			).orElse(Set.of());
		}
		else if (role.trajectory) {
			return new Trajectory(role.dirs, friends, enemies).from(pos);
		} else {
			Set<Pos> allPoss = role.dirs.stream()
				.map(dir -> dir.apply(pos))
				.filter(Optional::isPresent).map(Optional::get)
				.collect(Collectors.toSet());
			allPoss.removeAll(friends);
			return allPoss;
		}
	}

	public boolean threatens(Pos to) {
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
}
