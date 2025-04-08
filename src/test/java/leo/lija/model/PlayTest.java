package leo.lija.model;

import leo.lija.format.Visual;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.util.Pair;

import static leo.lija.model.Pos.A1;
import static leo.lija.model.Pos.A2;
import static leo.lija.model.Pos.A3;
import static leo.lija.model.Pos.A5;
import static leo.lija.model.Pos.A6;
import static leo.lija.model.Pos.B1;
import static leo.lija.model.Pos.B4;
import static leo.lija.model.Pos.B7;
import static leo.lija.model.Pos.B8;
import static leo.lija.model.Pos.C1;
import static leo.lija.model.Pos.C2;
import static leo.lija.model.Pos.C3;
import static leo.lija.model.Pos.C4;
import static leo.lija.model.Pos.C5;
import static leo.lija.model.Pos.C6;
import static leo.lija.model.Pos.C7;
import static leo.lija.model.Pos.C8;
import static leo.lija.model.Pos.D1;
import static leo.lija.model.Pos.D2;
import static leo.lija.model.Pos.D3;
import static leo.lija.model.Pos.D4;
import static leo.lija.model.Pos.D5;
import static leo.lija.model.Pos.D7;
import static leo.lija.model.Pos.D8;
import static leo.lija.model.Pos.E1;
import static leo.lija.model.Pos.E2;
import static leo.lija.model.Pos.E3;
import static leo.lija.model.Pos.E4;
import static leo.lija.model.Pos.E5;
import static leo.lija.model.Pos.E6;
import static leo.lija.model.Pos.E7;
import static leo.lija.model.Pos.E8;
import static leo.lija.model.Pos.F1;
import static leo.lija.model.Pos.F3;
import static leo.lija.model.Pos.F4;
import static leo.lija.model.Pos.F6;
import static leo.lija.model.Pos.F8;
import static leo.lija.model.Pos.G1;
import static leo.lija.model.Pos.G4;
import static leo.lija.model.Pos.G5;
import static leo.lija.model.Pos.G8;
import static leo.lija.model.Pos.H1;
import static leo.lija.model.Pos.H2;
import static leo.lija.model.Pos.H3;
import static leo.lija.model.Pos.H5;
import static leo.lija.model.Pos.H6;
import static leo.lija.model.Pos.H7;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("playing a game should")
public class PlayTest {

	Visual visual = new Visual();

	@Nested
	@DisplayName("opening one")
	class Opening1 {

		Situation situation = new Situation().playMoves(
			Pair.of(E2, E4),
			Pair.of(E7, E5),
			Pair.of(F1, C4),
			Pair.of(G8, F6),
			Pair.of(D2, D3),
			Pair.of(C7, C6),
			Pair.of(C1, G5),
			Pair.of(H7, H6)
		);

		@Test
		@DisplayName("current situation")
		void currentSituation() {
			assertThat(visual.newLine(situation.board.visual())).isEqualTo("""
rnbqkb r
pp p pp
  p  n p
    p B
  B P
   P
PPP  PPP
RN QK NR
""");
		}

		@Test
		@DisplayName("after recapture")
		void afterRecapture() {
			Situation newSituation = situation.playMoves(
				Pair.of(G5, F6),
				Pair.of(D8, F6)
			);
			assertThat(visual.newLine(newSituation.board.visual())).isEqualTo("""
rnb kb r
pp p pp
  p  q p
    p
  B P
   P
PPP  PPP
RN QK NR
""");
		}
	}

	@Test
	@DisplayName("Deep Blue vs Kasparov 1")
	void deepBlueKasparov1() {

		Situation situation = new Situation().playMoves(
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
		assertThat(visual.newLine(situation.board.visual())).isEqualTo("""
r   k  r
pp   ppp
  n pn
   q   b
 b P
    BN P
PP  BPP
RN Q RK
""");
	}

	@Test
	@DisplayName("Peruvian Immortal")
	void peruvianImmortal() {
		Situation situation = new Situation().playMoves(
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
		assertThat(visual.newLine(situation.board.visual())).isEqualTo("""
  kr  nr
p  n ppp
B p p

 P P B
  N    P
 PPK PP
       q
""");
	}
}
