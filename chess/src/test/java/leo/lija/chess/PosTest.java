package leo.lija.chess;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static leo.lija.chess.Pos.A4;
import static leo.lija.chess.Pos.B4;
import static leo.lija.chess.Pos.C4;
import static leo.lija.chess.Pos.D4;
import static leo.lija.chess.Pos.D5;
import static leo.lija.chess.Pos.E4;
import static leo.lija.chess.Pos.F4;
import static leo.lija.chess.Pos.G4;
import static leo.lija.chess.Pos.H4;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Position should")
class PosTest {

    @Test
    @DisplayName("be used to derive a relative list of positions")
    void relativeList() {
        assertThat(D4.multShiftLeft((p) -> false)).containsExactlyInAnyOrder(C4, B4, A4);
        assertThat(D4.multShiftRight((p) -> false)).containsExactlyInAnyOrder(E4, F4, G4, H4);
        assertThat(D4.multShiftLeft((p) -> p.equals(C4))).containsExactly(C4);
        assertThat(D4.multShiftRight((p) -> p.equals(G4))).containsExactlyInAnyOrder(E4, F4, G4);
    }

    @Test
    @DisplayName("be convertable to a string")
    void testToString() {
        assertEquals("d5", D5.toString());
    }
}
