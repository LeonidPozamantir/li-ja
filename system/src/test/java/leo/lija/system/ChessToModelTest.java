package leo.lija.system;

import leo.lija.chess.Game;
import leo.lija.chess.format.VisualFormat;
import leo.lija.system.entities.DbGame;
import leo.lija.system.entities.DbPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static leo.lija.chess.Color.WHITE;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("chess to model conversion")
class ChessToModelTest extends Fixtures {

    private VisualFormat visual = new VisualFormat();

    @Nested
    @DisplayName("new game")
    class newGame {
        Game game = newDbGame.toChess();
        @Test
        @Disabled("incorrect test - pieces are compared without sorting; will be fixed soon")
        void identity() {
            DbGame copy = newDbGame.copy();
            copy.update(game);
            assertThat(copy).isEqualTo(newDbGame);
        }
    }

    @Nested
    @DisplayName("played game")
    class PlayedGame {
        DbGame dbGame = dbGame4;
        Game game = new Game(visual.str2Obj("""
r   kb r
ppp pppp
  np
    P
   P   N
  P B P
P P  P P
R  QK  q
"""), WHITE, dbGame.getPgn());

        @Test
        @Disabled("incorrect expectation - cannot be LB both for white and black; test will be deleted in May")
        void identity() {
            DbGame copy = dbGame.copy();
            copy.update(game);
            assertThat(copy).isEqualTo(dbGame);
        }

        @Nested
        class Pieces {
            DbGame dbg2;

            @BeforeEach
            void setup() {
                dbg2 = newDbGame.copy();
                dbg2.update(game);
            }

            @Test
            @Disabled("incorrect expectation - cannot be LB both for white and black; test will be deleted in May")
            void white() {
                assertThat(dbg2.playerByColor("white").map(DbPlayer::getPs).map(ChessToModelTest.this::sortPs))
                    .isEqualTo(dbGame.playerByColor("white").map(DbPlayer::getPs).map(ChessToModelTest.this::sortPs));
            }

            @Test
            @Disabled("incorrect expectation - cannot be LB both for white and black; test will be deleted in May")
            void black() {
                assertThat(dbg2.playerByColor("black").map(DbPlayer::getPs).map(ChessToModelTest.this::sortPs))
                    .isEqualTo(dbGame.playerByColor("black").map(DbPlayer::getPs).map(ChessToModelTest.this::sortPs));
            }
        }
    }

    private String sortPs(String ps) {
        return Arrays.stream(ps.split(" ")).sorted().collect(Collectors.joining(" "));
    }
}
