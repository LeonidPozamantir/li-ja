package leo.lija.system.ai;

import leo.lija.chess.Game;
import leo.lija.chess.Move;
import leo.lija.chess.utils.Pair;
import leo.lija.system.Ai;
import leo.lija.system.entities.DbGame;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CraftyAi extends FenBased implements Ai  {

    private final CraftyServer server;

    public Pair<Game, Move> apply(DbGame dbGame) {

        Game oldGame = dbGame.toChess();
        String fen = toFen(oldGame, dbGame.getVariant());

        String strMove = server.apply(fen, dbGame.aiLevel().orElse(1));
        return applyFen(oldGame, strMove);
    }

}
