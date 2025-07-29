package leo.lija.system;

import io.vavr.collection.List;
import leo.lija.chess.Game;
import leo.lija.chess.Move;
import leo.lija.chess.format.VisualFormat;
import leo.lija.chess.utils.Pair;
import leo.lija.system.entities.DbGame;
import leo.lija.system.entities.DbPlayer;
import leo.lija.system.entities.event.MoveEvent;
import leo.lija.system.entities.event.PossibleMovesEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static leo.lija.chess.Color.BLACK;
import static leo.lija.chess.Color.WHITE;
import static leo.lija.chess.Pos.C3;
import static leo.lija.chess.Pos.D2;
import static leo.lija.chess.Pos.D4;
import static leo.lija.chess.Pos.F5;
import static leo.lija.chess.Pos.H1;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("chess to model conversion")
class ChessToModelTest extends Fixtures {

    private final VisualFormat visual = new VisualFormat();

    @Nested
    @DisplayName("new game")
    class newGame {
        DbGame dbGame = newDbGame;
        Game game = dbGame.toChess();

        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        class Identity {
            DbGame dbg2 = dbGame.copy();

            @BeforeAll
            void init() {
                dbg2.update(game, anyMove);
            }

            @Test
            @DisplayName("white pieces")
            void whitePieces() {
                assertThat(dbg2.playerByColor("white").map(DbPlayer::getPs).map(ChessToModelTest.this::sortPs)).isEqualTo(
                    dbGame.playerByColor("white").map(DbPlayer::getPs).map(ChessToModelTest.this::sortPs)
                );
            }

            @Test
            @DisplayName("black pieces")
            void blackPieces() {
                assertThat(dbg2.playerByColor("white").map(DbPlayer::getPs).map(ChessToModelTest.this::sortPs)).isEqualTo(
                    dbGame.playerByColor("white").map(DbPlayer::getPs).map(ChessToModelTest.this::sortPs)
                );
            }
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
"""), WHITE, dbGame.getPgn(), Optional.empty(), List.of(
            Pair.of(C3, BLACK.knight()),
            Pair.of(F5, BLACK.bishop()),
            Pair.of(F5, WHITE.bishop()),
            Pair.of(C3, WHITE.knight()),
            Pair.of(H1, WHITE.rook())
        ));

        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        class Identity {
            DbGame dbg2;

            @BeforeAll
            void setup() {
                dbg2 = dbGame.copy();
                dbg2.update(game, anyMove);
            }

            @Test
            void white() {
                assertThat(dbg2.playerByColor("white").map(DbPlayer::getPs).map(ChessToModelTest.this::sortPs))
                    .isEqualTo(dbGame.playerByColor("white").map(DbPlayer::getPs).map(ChessToModelTest.this::sortPs));
            }

            @Test
            void black() {
                assertThat(dbg2.playerByColor("black").map(DbPlayer::getPs).map(ChessToModelTest.this::sortPs))
                    .isEqualTo(dbGame.playerByColor("black").map(DbPlayer::getPs).map(ChessToModelTest.this::sortPs));
            }
        }

        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        @DisplayName("new pieces positions")
        class NewPositions {
            DbGame dbg2;

            @BeforeAll
            void setup() {
                dbg2 = newDbGame.copy();
                dbg2.update(game, anyMove);
            }

            @Test
            void white() {
                assertThat(dbg2.playerByColor("white").map(DbPlayer::getPs).map(ChessToModelTest.this::sortPs))
                    .isEqualTo(dbGame.playerByColor("white").map(DbPlayer::getPs).map(ChessToModelTest.this::sortPs));
            }

            @Test
            void black() {
                assertThat(dbg2.playerByColor("black").map(DbPlayer::getPs).map(ChessToModelTest.this::sortPs))
                    .isEqualTo(dbGame.playerByColor("black").map(DbPlayer::getPs).map(ChessToModelTest.this::sortPs));
            }
        }
    }

    @Nested
    @DisplayName("update events")
    class UpdateEvents {

        @Test
        @DisplayName("simple move")
        void simpleMove() {
            DbGame dbGame = newDbGameWithoutEvents;
            Game game = newDbGame.toChess();
            Pair<Game, Move> newGameMove = game.apply(D2, D4, null);
            dbGame.update(newGameMove.getFirst(), newGameMove.getSecond());
            assertThat(dbGame.playerByColor("white").map(DbPlayer::eventStack).get().getEvents()).containsExactly(
                Pair.of(1, new MoveEvent(D2, D4, WHITE)),
                Pair.of(2, new PossibleMovesEvent(Map.of()))
            );
        }
    }

    private String sortPs(String ps) {
        return Arrays.stream(ps.split(" ")).sorted().collect(Collectors.joining(" "));
    }
}
