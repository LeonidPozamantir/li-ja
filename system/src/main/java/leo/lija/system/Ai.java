package leo.lija.system;

import leo.lija.chess.Game;
import leo.lija.chess.Move;
import leo.lija.chess.utils.Pair;
import leo.lija.system.entities.DbGame;

public interface Ai {

    Pair<Game, Move> apply(DbGame dbGame);
}
