package leo.lija.chess.format;

import leo.lija.chess.Game;
import leo.lija.chess.RichGame;
import leo.lija.chess.utils.Pair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static leo.lija.chess.Pos.A1;
import static leo.lija.chess.Pos.A2;
import static leo.lija.chess.Pos.A3;
import static leo.lija.chess.Pos.A4;
import static leo.lija.chess.Pos.A5;
import static leo.lija.chess.Pos.A6;
import static leo.lija.chess.Pos.B1;
import static leo.lija.chess.Pos.B4;
import static leo.lija.chess.Pos.B5;
import static leo.lija.chess.Pos.B7;
import static leo.lija.chess.Pos.B8;
import static leo.lija.chess.Pos.C1;
import static leo.lija.chess.Pos.C2;
import static leo.lija.chess.Pos.C3;
import static leo.lija.chess.Pos.C4;
import static leo.lija.chess.Pos.C6;
import static leo.lija.chess.Pos.C7;
import static leo.lija.chess.Pos.C8;
import static leo.lija.chess.Pos.D1;
import static leo.lija.chess.Pos.D2;
import static leo.lija.chess.Pos.D4;
import static leo.lija.chess.Pos.D5;
import static leo.lija.chess.Pos.D7;
import static leo.lija.chess.Pos.D8;
import static leo.lija.chess.Pos.E1;
import static leo.lija.chess.Pos.E2;
import static leo.lija.chess.Pos.E3;
import static leo.lija.chess.Pos.E4;
import static leo.lija.chess.Pos.E6;
import static leo.lija.chess.Pos.E7;
import static leo.lija.chess.Pos.E8;
import static leo.lija.chess.Pos.F1;
import static leo.lija.chess.Pos.F3;
import static leo.lija.chess.Pos.F4;
import static leo.lija.chess.Pos.F8;
import static leo.lija.chess.Pos.G1;
import static leo.lija.chess.Pos.G4;
import static leo.lija.chess.Pos.H1;
import static leo.lija.chess.Pos.H2;
import static leo.lija.chess.Pos.H3;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("complete a game to pgn")
class PgnDumpTest {

    @Nested
    @DisplayName("move list")
    class onlyMoves {

        Game gioachinoGreco = RichGame.newGame().playMoves(Pair.of(D2, D4), Pair.of(D7, D5), Pair.of(C2, C4), Pair.of(D5, C4), Pair.of(E2, E3), Pair.of(B7, B5),
            Pair.of(A2, A4), Pair.of(C7, C6), Pair.of(A4, B5), Pair.of(C6, B5), Pair.of(D1, F3));
        Game peruvianImmortal = RichGame.newGame().playMoves(Pair.of(E2, E4), Pair.of(D7, D5), Pair.of(E4, D5), Pair.of(D8, D5), Pair.of(B1, C3), Pair.of(D5, A5), Pair.of(D2, D4),
            Pair.of(C7, C6), Pair.of(G1, F3), Pair.of(C8, G4), Pair.of(C1, F4), Pair.of(E7, E6), Pair.of(H2, H3), Pair.of(G4, F3), Pair.of(D1, F3), Pair.of(F8, B4),
            Pair.of(F1, E2), Pair.of(B8, D7), Pair.of(A2, A3), Pair.of(E8, C8), Pair.of(A3, B4), Pair.of(A5, A1), Pair.of(E1, D2), Pair.of(A1, H1), Pair.of(F3, C6),
            Pair.of(B7, C6), Pair.of(E2, A6));

        @Test
        @DisplayName("Gioachino Greco")
        void greco() {
            assertThat(gioachinoGreco.getPgnMoves()).isEqualTo("d4 d5 c4 dxc4 e3 b5 a4 c6 axb5 cxb5 Qf3");
        }

        @Test
        @DisplayName("Peruvian Immortal")
        void peruvian() {
            assertThat(peruvianImmortal.getPgnMoves()).isEqualTo("e4 d5 exd5 Qxd5 Nc3 Qa5 d4 c6 Nf3 Bg4 Bf4 e6 h3 Bxf3 Qxf3 Bb4 Be2 Nd7 a3 O-O-O axb4 Qxa1+ Kd2 Qxh1 Qxc6+ bxc6 Ba6#");
        }
    }
}