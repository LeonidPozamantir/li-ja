package leo;

import leo.lija.Game;
import leo.lija.utils.Pair;
import org.openjdk.jmh.annotations.Benchmark;

import static leo.lija.Pos.A1;
import static leo.lija.Pos.A2;
import static leo.lija.Pos.A3;
import static leo.lija.Pos.A5;
import static leo.lija.Pos.A6;
import static leo.lija.Pos.B1;
import static leo.lija.Pos.B4;
import static leo.lija.Pos.B7;
import static leo.lija.Pos.B8;
import static leo.lija.Pos.C1;
import static leo.lija.Pos.C2;
import static leo.lija.Pos.C3;
import static leo.lija.Pos.C5;
import static leo.lija.Pos.C6;
import static leo.lija.Pos.C7;
import static leo.lija.Pos.C8;
import static leo.lija.Pos.D1;
import static leo.lija.Pos.D2;
import static leo.lija.Pos.D4;
import static leo.lija.Pos.D5;
import static leo.lija.Pos.D7;
import static leo.lija.Pos.D8;
import static leo.lija.Pos.E1;
import static leo.lija.Pos.E2;
import static leo.lija.Pos.E3;
import static leo.lija.Pos.E4;
import static leo.lija.Pos.E6;
import static leo.lija.Pos.E7;
import static leo.lija.Pos.E8;
import static leo.lija.Pos.F1;
import static leo.lija.Pos.F3;
import static leo.lija.Pos.F4;
import static leo.lija.Pos.F6;
import static leo.lija.Pos.F8;
import static leo.lija.Pos.G1;
import static leo.lija.Pos.G4;
import static leo.lija.Pos.G8;
import static leo.lija.Pos.H1;
import static leo.lija.Pos.H2;
import static leo.lija.Pos.H3;
import static leo.lija.Pos.H5;

public class GameBenchmark {

    @Benchmark
    public void timeImmortal() {
        Game.newGame().playMoves(
            Pair.of(E2, E4),
            Pair.of(D7, D5),
            Pair.of(E4, D5),
            Pair.of(D8, D5),
            Pair.of(B1, C3),
            Pair.of(D5, A5),
            Pair.of(D2, D4),
            Pair.of(C7, C6),
            Pair.of(G1, F3),
            Pair.of(C8, G4),
            Pair.of(C1, F4),
            Pair.of(E7, E6),
            Pair.of(H2, H3),
            Pair.of(G4, F3),
            Pair.of(D1, F3),
            Pair.of(F8, B4),
            Pair.of(F1, E2),
            Pair.of(B8, D7),
            Pair.of(A2, A3),
            Pair.of(E8, C8),
            Pair.of(A3, B4),
            Pair.of(A5, A1),
            Pair.of(E1, D2),
            Pair.of(A1, H1),
            Pair.of(F3, C6),
            Pair.of(B7, C6),
            Pair.of(E2, A6)
        );
    }

    @Benchmark
    public void timeDeepBlue() {
        Game.newGame().playMoves(
            Pair.of(E2, E4),
            Pair.of(C7, C5),
            Pair.of(C2, C3),
            Pair.of(D7, D5),
            Pair.of(E4, D5),
            Pair.of(D8, D5),
            Pair.of(D2, D4),
            Pair.of(G8, F6),
            Pair.of(G1, F3),
            Pair.of(C8, G4),
            Pair.of(F1, E2),
            Pair.of(E7, E6),
            Pair.of(H2, H3),
            Pair.of(G4, H5),
            Pair.of(E1, G1),
            Pair.of(B8, C6),
            Pair.of(C1, E3),
            Pair.of(C5, D4),
            Pair.of(C3, D4),
            Pair.of(F8, B4)
        );
    }

}
