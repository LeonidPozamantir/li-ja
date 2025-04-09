package leo.lija.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static leo.lija.model.Pos.A4;
import static leo.lija.model.Pos.B4;
import static leo.lija.model.Pos.C4;
import static leo.lija.model.Pos.C5;
import static leo.lija.model.Pos.D1;
import static leo.lija.model.Pos.D2;
import static leo.lija.model.Pos.D3;
import static leo.lija.model.Pos.D4;
import static leo.lija.model.Pos.D5;
import static leo.lija.model.Pos.D6;
import static leo.lija.model.Pos.D7;
import static leo.lija.model.Pos.D8;
import static leo.lija.model.Pos.E4;
import static leo.lija.model.Pos.E5;
import static leo.lija.model.Pos.F4;
import static leo.lija.model.Pos.G4;
import static leo.lija.model.Pos.G5;
import static leo.lija.model.Pos.H4;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Position should")
class PosTest {

    @Test
    @DisplayName("be used to derive a relative position on the board")
    void relativePosition() {
        assertEquals(Optional.of(D6), D5.shiftUp(1));
        assertEquals(Optional.of(D4), D5.shiftDown(1));
        assertEquals(Optional.of(C5), D5.shiftLeft(1));
        assertEquals(Optional.of(E5), D5.shiftRight(1));
    }

    @Test
    @DisplayName("be used to calculate a non-position off the edge of the board")
    void offTheBoard() {
        assertEquals(Optional.empty(), D5.shiftUp(4));
        assertEquals(Optional.empty(), D5.shiftDown(5));
        assertEquals(Optional.empty(), D5.shiftLeft(5));
        assertEquals(Optional.empty(), D5.shiftRight(5));
    }

    @Test
    @DisplayName("be able to calculate a relative position with negative numbers")
    void relativePositionWithNegative() {
        assertEquals(Optional.of(D3), D5.shiftUp(-2));
        assertEquals(Optional.of(D8), D5.shiftDown(-3));
        assertEquals(Optional.of(G5), D5.shiftLeft(-3));
        assertEquals(Optional.of(C5), D5.shiftRight(-1));
    }

    @Test
    @DisplayName("be used to calculate a non-position off the edge of the board using negative numbers")
    void offTheBoardWithNegative() {
        assertEquals(Optional.empty(), D5.shiftUp(-6));
        assertEquals(Optional.empty(), D5.shiftDown(-6));
        assertEquals(Optional.empty(), D5.shiftLeft(-6));
        assertEquals(Optional.empty(), D5.shiftRight(-6));
    }

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
