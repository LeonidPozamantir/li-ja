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
	List<String> positionHashes,
	boolean whiteCastleKingSide,
	boolean whiteCastleQueenSide,
	boolean blackCastleKingSide,
	boolean blackCastleQueenSide
) {
	public History() {
		this(Optional.empty());
	}

	public History(Optional<Pair<Pos, Pos>> lastMove) {
		this(lastMove, List.of());
	}

	public History(Optional<Pair<Pos, Pos>> lastMove, List<String> positionHashes) {
		this(lastMove, positionHashes, true, true, true, true);
	}

	public boolean isLastMove(Pos p1, Pos p2) {
		return lastMove.isPresent() && lastMove.get().getFirst().equals(p1) && lastMove.get().getSecond().equals(p2);
	}

	public boolean threefoldRepetition() {
		return positionHashes.size() > 6 && positionHashes.headOption().map(hash ->
			positionHashes.count(h -> h.equals(hash)) >= 3
		).getOrElse(false);
	}

	public boolean canCastle(Color color, Side side) {
		if (side == KING_SIDE && color == WHITE) return whiteCastleKingSide;
		if (side == QUEEN_SIDE && color == WHITE) return whiteCastleQueenSide;
		if (side == KING_SIDE && color == BLACK) return blackCastleKingSide;
		if (side == QUEEN_SIDE && color == BLACK) return blackCastleQueenSide;
		return false;
	}

	public boolean canCastle(Color color) {
		return canCastle(color, KING_SIDE) || canCastle(color, QUEEN_SIDE);
	}

	public History withoutCastles(Color color) {
		return switch (color) {
			case WHITE -> new History(lastMove, positionHashes, false, false, blackCastleKingSide, blackCastleQueenSide);
			case BLACK -> new History(lastMove, positionHashes, whiteCastleKingSide, whiteCastleQueenSide, false, false);
		};
	}

	public History withoutAnyCastles() {
		return new History(lastMove, positionHashes, false, false, false, false);
	}

	public History withoutCastle(Color color, Side side) {
		if (side == KING_SIDE && color == WHITE) return new History(lastMove, positionHashes, false, whiteCastleQueenSide, blackCastleKingSide, blackCastleQueenSide);
		if (side == QUEEN_SIDE && color == WHITE) return new History(lastMove, positionHashes, whiteCastleKingSide, false, blackCastleKingSide, blackCastleQueenSide);
		if (side == KING_SIDE && color == BLACK) return new History(lastMove, positionHashes, whiteCastleKingSide, whiteCastleQueenSide, false, blackCastleQueenSide);
		if (side == QUEEN_SIDE && color == BLACK) return new History(lastMove, positionHashes, whiteCastleKingSide, whiteCastleQueenSide, blackCastleKingSide, false);
		return null;
	}

	public History withoutPositionHashes() {
		return new History(lastMove, List.of(), whiteCastleKingSide, whiteCastleQueenSide, blackCastleKingSide, blackCastleQueenSide);
	}

	public History withNewPositionHash(String hash) {
		return new History(lastMove, positionHashesWith(hash), whiteCastleKingSide, whiteCastleQueenSide, blackCastleKingSide, blackCastleQueenSide);
	}

	public List<String> positionHashesWith(String hash) {
		return positionHashes.prepend(hash.substring(0, HASH_SIZE));
	}

	public String castleNotation() {
		String notation = (whiteCastleKingSide ? "K" : "") +
			(whiteCastleQueenSide ? "Q" : "") +
			(blackCastleKingSide ? "k" : "") +
			(blackCastleQueenSide ? "q" : "");
		return notation.isEmpty() ? "-" : notation;
	}

	public History withLastMove(Pos orig, Pos dest) {
		return new History(Optional.of(Pair.of(orig, dest)), positionHashes, whiteCastleKingSide, whiteCastleQueenSide, blackCastleKingSide, blackCastleQueenSide);
	}

	public static final int HASH_SIZE = 5;

	public static History castle(Color color, boolean kingSide, boolean queenSide) {
		return switch (color) {
			case WHITE -> new History(Optional.empty(), List.empty(), kingSide, queenSide, true, true);
			case BLACK -> new History(Optional.empty(), List.empty(), true, true, kingSide, queenSide);
		};
	}

	public static History noCastle() {
		return new History().withoutCastles(WHITE).withoutCastles(BLACK);
	}
}
