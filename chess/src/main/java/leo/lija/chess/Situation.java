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

	public List<Actor> actors() {
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

	public Move move(Pos from, Pos to, Role promotion) {
		if (!promotion.promotable) throw new ChessRulesException("Cannot promote to %s".formatted(promotion));

		Optional<Actor> actor = board.actorAt(from);
		Optional<Move> someMove = actor
			.filter(a -> a.is(color))
			.flatMap(a -> a.moves().stream()
				.filter(m -> m.dest().equals(to))
				.findFirst()
			);

		Optional<Move> resMove = promotion == QUEEN
			? someMove
			: someMove.map(Move::after)
				.filter(b1 -> b1.count(color.queen()) > board.count(color.queen()))
				.flatMap(b1 -> b1.take(to))
				.flatMap(b2 -> b2.place(color.of(promotion), to))
				.map(b3 -> someMove.get().withAfter(b3).withPromotion(Optional.of(promotion)));

			return resMove.orElseThrow(() -> new ChessRulesException("Illegal move %s->%s".formatted(from, to)));
	}

	public Situation as(Color newColor) {
		return new Situation(board, newColor);
	}
}
