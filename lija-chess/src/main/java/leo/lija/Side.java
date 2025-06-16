package leo.lija;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

@RequiredArgsConstructor
public enum Side {
	KING_SIDE(7, 6, (pos, board) -> pos.multShiftRight(p -> board.occupations().contains(p))),
	QUEEN_SIDE(3, 4, (pos, board) -> pos.multShiftLeft(p -> board.occupations().contains(p)));

	public final int castledKingX;
	public final int castledRookX;
	public final BiFunction<Pos, Board, List<Pos>> tripToRook;

	public static final List<Side> ALL = List.of(KING_SIDE, QUEEN_SIDE);

	public static Optional<Side> kingRookSide(Pos kingPos, Pos rookPos) {
		if (kingPos.getY() != rookPos.getY()) {
			return Optional.empty();
		}
		return Optional.of(kingPos.getX() > rookPos.getX() ? QUEEN_SIDE : KING_SIDE);
	}
}
