package leo.lija.model;

import leo.lija.exceptions.ChessRulesException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class Situation {

	final Board board;
	final Color color;

	public Situation() {
		this(new Board(), Color.WHITE);
	}

	List<Actor> actors() {
		return board.actorsOf(color);
	}

	public Map<Pos, Set<Pos>> moves() {
		return actors().stream()
			.filter(a -> !a.moves().isEmpty())
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

	public Situation playMove(Pos from, Pos to) {
		Actor actor = board.actorAt(from);
		Board newBoard = actor.implications().get(to);
		if (!actor.is(color) || newBoard == null) throw new ChessRulesException("Illegal move %s->%s".formatted(from, to));
		return new Situation(newBoard, color.getOpposite());
	}

	public Situation playMove(Pair<Pos, Pos> move) {
		return playMove(move.getFirst(), move.getSecond());
	}

	@SafeVarargs
	public final Situation playMoves(Pair<Pos, Pos>... moves) {
		return Arrays.stream(moves)
			.reduce(this, Situation::playMove, (s1, s2) -> s1);
	}
}
