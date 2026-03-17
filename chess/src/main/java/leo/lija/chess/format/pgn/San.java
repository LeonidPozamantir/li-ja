package leo.lija.chess.format.pgn;

import leo.lija.chess.Game;
import leo.lija.chess.Move;

// Standard algebraic notation
public interface San {

    Move apply(Game game);
}
