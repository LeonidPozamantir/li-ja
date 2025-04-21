package leo.lija.model;

import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.function.BiFunction;

@RequiredArgsConstructor
public enum Side {
	KingSide(7, 6, (pos, board) -> pos.multShiftRight(p -> board.occupations().contains(p))),
	QueenSide(3, 4, (pos, board) -> pos.multShiftLeft(p -> board.occupations().contains(p)));

	public final int castledKingX;
	public final int castledRookX;
	public final BiFunction<Pos, Board, List<Pos>> tripToRook;
}
