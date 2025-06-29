package leo.lija.chess;


import io.vavr.collection.List;
import leo.lija.chess.utils.Pair;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

import static leo.lija.chess.Color.BLACK;
import static leo.lija.chess.Color.WHITE;
import static leo.lija.chess.Side.KING_SIDE;
import static leo.lija.chess.Side.QUEEN_SIDE;

public record History (
	Optional<Pair<Pos, Pos>> lastMove,
	EnumMap<Color, Pair<Boolean, Boolean>> castles,
	List<String> positionHashes
) {
	public History() {
		this(Optional.empty());
	}

	public History(Optional<Pair<Pos, Pos>> lastMove) {
		this(lastMove, new EnumMap<>(Map.of(
			WHITE, Pair.of(true, true),
			BLACK, Pair.of(true, true)
		)), List.of());
	}

	public boolean canCastle(Color color, Side side) {
		Pair<Boolean, Boolean> castlesColor = colorCastles(color);
		return side == Side.KING_SIDE ? castlesColor.getFirst() : castlesColor.getSecond();
	}

	public boolean canCastle(Color color) {
		return canCastle(color, KING_SIDE) || canCastle(color, QUEEN_SIDE);
	}

	public History withoutCastle(Color color, Side side) {
		Pair<Boolean, Boolean> castlesColor = colorCastles(color);
		EnumMap<Color, Pair<Boolean, Boolean>> newCastles = new EnumMap<>(castles);
		newCastles.put(color, Pair.of(
			!side.equals(KING_SIDE) && castlesColor.getFirst(),
			!side.equals(QUEEN_SIDE) && castlesColor.getSecond()
		));
		return new History(lastMove, newCastles, positionHashes);
	}

	public History withoutCastles(Color color) {
		EnumMap<Color, Pair<Boolean, Boolean>> newCastles = new EnumMap<>(castles);
		newCastles.put(color, Pair.of(false, false));
		return new History(lastMove, newCastles, positionHashes);
	}

	public History withoutPositionHashes() {
		return new History(lastMove, castles, List.of());
	}

	public History withNewPositionHash(String hash) {
		return new History(lastMove, castles, positionHashes.prepend(hash));
	}

	private Pair<Boolean, Boolean> colorCastles(Color color) {
		return castles.getOrDefault(color, Pair.of(true, true));
	}

	public static History castle(Color color, boolean kingSide, boolean queenSide) {
		return new History(Optional.empty(), new EnumMap<>(Map.of(color, Pair.of(kingSide, queenSide))), List.of());
	}

	public static History noCastle() {
		return new History(Optional.empty(), new EnumMap<>(Color.class), List.of());
	}
}
