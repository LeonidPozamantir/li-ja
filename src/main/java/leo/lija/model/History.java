package leo.lija.model;


import org.springframework.data.util.Pair;

import java.util.Map;
import java.util.Optional;

import static leo.lija.model.Color.BLACK;
import static leo.lija.model.Color.WHITE;

public record History (
	Optional<Pair<Pos, Pos>> lastMove,
	Map<Color, Pair<Boolean, Boolean>> castles
) {
	public History() {
		this(Optional.empty());
	}

	public History(Optional<Pair<Pos, Pos>> lastMove) {
		this(lastMove, Map.of(WHITE, Pair.of(false, false), BLACK, Pair.of(false, false)));
	}

	public boolean isLastMove(Pos p1, Pos p2) {
		return lastMove.map(p -> p.getFirst().equals(p1) && p.getSecond().equals(p2)).orElse(false);
	}

	public boolean canCastleKingSide(Color color) {
		return castles.get(color).getFirst();
	}

	public boolean canCastleQueenSide(Color color) {
		return castles.get(color).getSecond();
	}

	public static History castle(Color color, boolean kingSide, boolean queenSide) {
		return new History(Optional.empty(), Map.of(color, Pair.of(kingSide, queenSide)));
	}
}
