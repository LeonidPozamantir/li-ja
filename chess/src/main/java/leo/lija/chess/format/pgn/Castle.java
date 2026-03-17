package leo.lija.chess.format.pgn;

import leo.lija.chess.Actor;
import leo.lija.chess.Game;
import leo.lija.chess.Move;
import leo.lija.chess.Pos;
import leo.lija.chess.Side;
import leo.lija.chess.exceptions.ChessException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Castle implements San {

    private final Side side;

    @Override
    public Move apply(Game game) {
        Pos kingPos = game.getBoard().kingPosOf(game.getPlayer())
            .orElseThrow(() -> new ChessException("No king found"));
        Actor actor = game.getBoard().actorAt(kingPos)
            .orElseThrow(() -> new ChessException("No actor found"));
        return actor.castleOn(side)
            .orElseThrow(() -> new ChessException("Cannot castle"));
    }
}
