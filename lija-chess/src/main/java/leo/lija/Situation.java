package leo.lija;

import leo.lija.exceptions.ChessRulesException;
import leo.lija.utils.Pair;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static leo.lija.Role.QUEEN;

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
			.filter(a -> !a.destinations().isEmpty())
			.collect(Collectors.toMap(Actor::getPos, Actor::destinations));
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

	@SafeVarargs
	public final Situation playMoves(Pair<Pos, Pos>... moves) {
		return Arrays.stream(moves)
			.reduce(this, (s, m) -> s.playMove(m.getFirst(), m.getSecond()), (s1, s2) -> s1);
	}

	public Situation playMove(Pos from, Pos to) {
		return playMove(from, to, QUEEN);
	}

	public Situation playMove(Pos from, Pos to, Role promotion) {
		if (!promotion.promotable) throw new ChessRulesException("Cannot promote to %s".formatted(promotion));
		Actor actor = board.actorAt(from);
		Board newBoard = actor.moves().get(to);
		if (!actor.is(color) || newBoard == null) throw new ChessRulesException("Illegal move %s->%s".formatted(from, to));

		if (promotion == QUEEN) return newBoard.as(color.getOpposite());
		return Optional.of(newBoard)
			.filter(b1 -> b1.count(color.queen()) > board.count(color.queen()))
			.flatMap(b1 -> b1.take(to))
			.flatMap(b2 -> b2.place(color.of(promotion), to))
			.map(b3 -> b3.as(color.getOpposite()))
			.orElseThrow(() -> new ChessRulesException("Illegal move %s->%s".formatted(from, to)));
	}

	public Situation as(Color newColor) {
		return new Situation(board, newColor);
	}
}
