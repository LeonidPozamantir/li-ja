package leo.lija.model;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class Situation {

	private final Board board;
	private final Color color;

	List<Actor> actors() {
		return board.actorsOf(color);
	}

	public Map<Pos, Set<Pos>> moves() {
		return actors().stream()
			.collect(Collectors.toMap(Actor::getPos, Actor::moves));
	}

	public boolean check() {
		return board.kingPosOf(color).map(king -> board.actorsOf(color.getOpposite()).stream().anyMatch(a -> a.threatens(king)))
			.orElse(false);
	}

	public boolean checkmate() {
		return check() && moves().isEmpty();
	}

	public boolean stalemate() {
		return !check() && moves().isEmpty();
	}
}
