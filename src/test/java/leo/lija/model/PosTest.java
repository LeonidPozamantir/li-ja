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
        assertEquals(D5.shiftUp(1), Optional.of(D6));
        assertEquals(D5.shiftDown(1), Optional.of(D4));
        assertEquals(D5.shiftLeft(1), Optional.of(C5));
        assertEquals(D5.shiftRight(1), Optional.of(E5));
    }

    @Test
    @DisplayName("be used to calculate a non-position off the edge of the board")
    void offTheBoard() {
        assertEquals(D5.shiftUp(4), Optional.empty());
        assertEquals(D5.shiftDown(5), Optional.empty());
        assertEquals(D5.shiftLeft(5), Optional.empty());
        assertEquals(D5.shiftRight(5), Optional.empty());
    }

    @Test
    @DisplayName("be able to calculate a relative position with negative numbers")
    void relativePositionWithNegative() {
        assertEquals(D5.shiftUp(-2), Optional.of(D3));
        assertEquals(D5.shiftDown(-3), Optional.of(D8));
        assertEquals(D5.shiftLeft(-3), Optional.of(G5));
        assertEquals(D5.shiftRight(-1), Optional.of(C5));
    }

    @Test
    @DisplayName("be used to calculate a non-position off the edge of the board using negative numbers")
    void offTheBoardWithNegative() {
        assertEquals(D5.shiftUp(-6), Optional.empty());
        assertEquals(D5.shiftDown(-6), Optional.empty());
        assertEquals(D5.shiftLeft(-6), Optional.empty());
        assertEquals(D5.shiftRight(-6), Optional.empty());
    }

    @Test
    @DisplayName("be used to derive a relative list of positions")
    void relativeList() {
        assertThat(D4.multShiftUp(3)).contains(D4, D5, D6, D7);
        assertThat(D4.multShiftDown(3)).contains(D4, D3, D2, D1);
        assertThat(D4.multShiftLeft(3)).contains(D4, C4, B4, A4);
        assertThat(D4.multShiftRight(3)).contains(D4, E4, F4, G4);
    }

    @Test
    @DisplayName("be used to derive a relative list of positions not including those off the board")
    void relativeListNotOffTheBoard() {
        assertThat(D4.multShiftUp(8)).contains(D4, D5, D6, D7, D8);
        assertThat(D4.multShiftDown(8)).contains(D4, D3, D2, D1);
        assertThat(D4.multShiftLeft(8)).contains(D4, C4, B4, A4);
        assertThat(D4.multShiftRight(8)).contains(D4, E4, F4, G4, H4);
    }

    @Test
    @DisplayName("be convertable to a string")
    void testToString() {
        assertEquals(D5.toString(), "d5");
    }
}
