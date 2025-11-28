package leo.lija.app.ai;

import leo.lija.chess.Game;
import leo.lija.chess.History;
import leo.lija.chess.Move;
import leo.lija.chess.Pos;
import leo.lija.chess.format.Fen;
import leo.lija.chess.utils.Pair;
import leo.lija.app.entities.Variant;

public class FenBased {

    protected Pair<Game, Move> applyFen(Game game, String strMove) {
        Pos orig = Pos.posAt(strMove.substring(0, 2)).get();
        Pos dest = Pos.posAt(strMove.substring(2, 4)).get();
        return game.apply(orig, dest);
    }

    protected String toFen(Game game, Variant variant) {
        return Fen.obj2Str(variant == Variant.CHESS960
                ? game.updateBoard(board -> board.updateHistory(History::withoutAnyCastles))
                : game
        );
    }

}
