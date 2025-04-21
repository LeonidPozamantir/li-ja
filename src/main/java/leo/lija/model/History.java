package leo.lija.model;


import org.springframework.data.util.Pair;

import java.util.Map;
import java.util.Optional;

public record History (
	Optional<Pair<Pos, Pos>> lastMove,
	Map<Color, Pair<Boolean, Boolean>> castles
) {
	public History() {
		this(Optional.empty());
	}

	public History(Optional<Pair<Pos, Pos>> lastMove) {
		this(lastMove, Map.of());
	}

	public boolean canCastle(Color color, Side side) {
		Pair<Boolean, Boolean> castlesColor = castles.get(color);
		if (castlesColor == null) {
			return false;
		}
		return side == Side.KING_SIDE ? castlesColor.getFirst() : castlesColor.getSecond();
	}

	public static History castle(Color color, boolean kingSide, boolean queenSide) {
		return new History(Optional.empty(), Map.of(color, Pair.of(kingSide, queenSide)));
	}
}
