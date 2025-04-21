package leo.lija.model;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.BiFunction;

@RequiredArgsConstructor
public enum Side {
	KING_SIDE(7, 6, (pos, board) -> pos.multShiftRight(p -> board.occupations().contains(p))),
	QUEEN_SIDE(3, 4, (pos, board) -> pos.multShiftLeft(p -> board.occupations().contains(p)));

	public final int castledKingX;
	public final int castledRookX;
	public final BiFunction<Pos, Board, List<Pos>> tripToRook;
}
