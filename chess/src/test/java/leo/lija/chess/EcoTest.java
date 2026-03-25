package leo.lija.chess;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class EcoTest {

    private Optional<String> name(String g) {
        return Eco.openingOf(g).map(Opening::name);
    }

    @Test
    void g1() {
        String g = "d4 Nf6 e4 Nxe4 f3 Nd6 g3";
        assertThat(name(g)).contains("Queen's Pawn Game");
    }

    @Test
    void g2() {
        String g = "e4 e5 Nf3 Nf6";
        assertThat(name(g)).contains("Petrov Defense");
    }

    @Test
    void g3() {
        String g = "e4 e5";
        assertThat(name(g)).contains("King's Pawn Game");
    }

    @Test
    void g4() {
        String g = "e4 e5 b3 Nc6";
        assertThat(name(g)).contains("King's Pawn Game");
    }

    @Test
    void g5() {
        String g = "e4 e5 b3 Nc6 Nc3";
        assertThat(name(g)).contains("King's Pawn Game");
    }

}
