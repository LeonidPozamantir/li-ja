package leo.lija.chess;

import leo.lija.chess.utils.Pair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static leo.lija.chess.Color.BLACK;
import static leo.lija.chess.Color.WHITE;
import static org.assertj.core.api.Assertions.assertThat;

class EloTest {

    private EloCalculator calc = new EloCalculator();

    private EloCalculator.User user(int e, int n) {
        return new EloCalculator.User(e, n);
    }

    @Nested
    @DisplayName("calculate standard")
    class CalculateStandard {

        @Nested
        @DisplayName("with equal elo")
        class EqualElo {

            EloCalculator.User u1 = user(1400, 56);
            EloCalculator.User u2 = user(1400, 389);

            @Nested
            @DisplayName("p1 win")
            class P1Win {
                Pair<Integer, Integer> nu = calc.calculate(u1, u2, Optional.of(WHITE));

                @Test
                @DisplayName("new elos")
                void newElos() {
                    assertThat(nu).isEqualTo(Pair.of(1416, 1384));
                }

                @Test
                @DisplayName("conservation rule")
                void conservation() {
                    assertThat(nu.getFirst() - u1.elo() + nu.getSecond() - u2.elo()).isZero();
                }
            }

            @Nested
            @DisplayName("p1 loss")
            class P1Loss {
                Pair<Integer, Integer> nu = calc.calculate(u1, u2, Optional.of(BLACK));

                @Test
                @DisplayName("new elos")
                void newElos() {
                    assertThat(nu).isEqualTo(Pair.of(1384, 1416));
                }

                @Test
                @DisplayName("conservation rule")
                void conservation() {
                    assertThat(nu.getFirst() - u1.elo() + nu.getSecond() - u2.elo()).isZero();
                }
            }

            @Nested
            class Draw {
                Pair<Integer, Integer> nu = calc.calculate(u1, u2, Optional.empty());

                @Test
                @DisplayName("new elos")
                void newElos() {
                    assertThat(nu).isEqualTo(Pair.of(1400, 1400));
                }

                @Test
                @DisplayName("conservation rule")
                void conservation() {
                    assertThat(nu.getFirst() - u1.elo() + nu.getSecond() - u2.elo()).isZero();
                }
            }
        }

        @Nested
        class Loss {
            EloCalculator.User u1 = user(1613, 56);
            EloCalculator.User u2 = user(1388, 389);
            Pair<Integer, Integer> nu = calc.calculate(u1, u2, Optional.of(WHITE));

            @Test
            @DisplayName("new elos")
            void newElos() {
                assertThat(nu).isEqualTo(Pair.of(1620, 1381));
            }

            @Test
            @DisplayName("conservation rule")
            void conservation() {
                assertThat(nu.getFirst() - u1.elo() + nu.getSecond() - u2.elo()).isZero();
            }
        }

        @Nested
        class Provision {
            EloCalculator.User u1 = user(1613, 8);
            EloCalculator.User u2 = user(1388, 389);
            Pair<Integer, Integer> nu = calc.calculate(u1, u2, Optional.of(WHITE));

            @Test
            @DisplayName("new elos")
            void newElos() {
                assertThat(nu).isEqualTo(Pair.of(1628, 1381));
            }
        }

        @Nested
        @DisplayName("no provision")
        class NoProvision {
            EloCalculator.User u1 = user(1313, 1256);
            EloCalculator.User u2 = user(1158, 124);
            Pair<Integer, Integer> nu = calc.calculate(u1, u2, Optional.of(WHITE));

            @Test
            @DisplayName("new elos")
            void newElos() {
                assertThat(nu).isEqualTo(Pair.of(1322, 1149));
            }
        }
    }
}
