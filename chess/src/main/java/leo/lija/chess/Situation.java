package leo.lija.chess;

import leo.lija.chess.exceptions.ChessRulesException;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static leo.lija.chess.Role.QUEEN;

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

	public Map<Pos, List<Move>> moves() {
		return actors().stream()
			.filter(a -> !a.destinations().isEmpty())
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

	public Move playMove(Pos from, Pos to) {
		return playMove(from, to, QUEEN);
	}

	public Move playMove(Pos from, Pos to, Role promotion) {
		if (!promotion.promotable) throw new ChessRulesException("Cannot promote to %s".formatted(promotion));
		String illegalMoveException = "Illegal move %s->%s".formatted(from, to);
		Actor actor = board.actorAt(from);
		if (!actor.is(color)) throw new ChessRulesException(illegalMoveException);
		Move move = actor.moves().stream()
			.filter(m -> m.dest().equals(to))
			.findFirst().orElseThrow(() -> new ChessRulesException(illegalMoveException));

		if (promotion == QUEEN) return move;
		return Optional.of(move.after())
			.filter(b1 -> b1.count(color.queen()) > board.count(color.queen()))
			.flatMap(b1 -> b1.take(to))
			.flatMap(b2 -> b2.place(color.of(promotion), to))
			.map(b3 -> move.withAfter(b3).withPromotion(Optional.of(promotion)))
			.orElseThrow(() -> new ChessRulesException(illegalMoveException));
	}

	public Situation as(Color newColor) {
		return new Situation(board, newColor);
	}
}
