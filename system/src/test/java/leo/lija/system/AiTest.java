package leo.lija.system;

import leo.lija.chess.Board;
import leo.lija.chess.Game;
import leo.lija.chess.Move;
import leo.lija.chess.utils.Pair;
import leo.lija.system.entities.DbGame;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("the AI should")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AiTest extends Fixtures {

    protected Ai ai;
    protected String name;

    @Test
    @DisplayName("play the first move")
    void firstMove() {
        DbGame dbGame = newDbGame;
        Pair<Game, Move> gm = ai.apply(dbGame);
        Game game = gm.getFirst();
        assertThat(game.getBoard()).isNotEqualTo(new Board());
    }

    @Test
    @DisplayName("play 20 moves")
    void play20Moves() {
        DbGame dbg = newDbGame.copy();
        IntStream.rangeClosed(1, 20).forEach((i) -> {
            Pair<Game, Move> gm = ai.apply(dbg);
            Game game = gm.getFirst();
            Move move = gm.getSecond();
            dbg.update(game, move);
        });
        assertThat(dbg.getTurns()).isEqualTo(20);
    }
}
