package leo.lija.model;


import org.springframework.data.util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static leo.lija.model.Color.BLACK;
import static leo.lija.model.Color.WHITE;
import static leo.lija.model.Side.KING_SIDE;
import static leo.lija.model.Side.QUEEN_SIDE;

public record History (
	Optional<Pair<Pos, Pos>> lastMove,
	Map<Color, Pair<Boolean, Boolean>> castles
) {
	public History() {
		this(Optional.empty());
	}

	public History(Optional<Pair<Pos, Pos>> lastMove) {
		this(lastMove, Map.of(
			WHITE, Pair.of(true, true),
			BLACK, Pair.of(true, true)
		));
	}

	public boolean canCastle(Color color, Side side) {
		Pair<Boolean, Boolean> castlesColor = colorCastles(color);
		return side == Side.KING_SIDE ? castlesColor.getFirst() : castlesColor.getSecond();
	}

	public History withoutCastle(Color color, Side side) {
		Pair<Boolean, Boolean> castlesColor = colorCastles(color);
		Map<Color, Pair<Boolean, Boolean>> newCastles = new HashMap<>(castles);
		newCastles.put(color, Pair.of(
			!side.equals(KING_SIDE) && castlesColor.getFirst(),
			!side.equals(QUEEN_SIDE) && castlesColor.getSecond()
		));
		return new History(lastMove, newCastles);
	}

	public History withoutCastles(Color color) {
		Map<Color, Pair<Boolean, Boolean>> newCastles = new HashMap<>(castles);
		newCastles.put(color, Pair.of(false, false));
		return new History(lastMove, newCastles);
	}

	private Pair<Boolean, Boolean> colorCastles(Color color) {
		return castles.getOrDefault(color, Pair.of(true, true));
	}

	public static History castle(Color color, boolean kingSide, boolean queenSide) {
		return new History(Optional.empty(), Map.of(color, Pair.of(kingSide, queenSide)));
	}

	public static History noCastle() {
		return new History(Optional.empty(), Map.of());
	}
}
