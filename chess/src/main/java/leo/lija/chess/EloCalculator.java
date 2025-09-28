package leo.lija.chess;

import leo.lija.chess.utils.Pair;

import java.util.Optional;

import static leo.lija.chess.Color.BLACK;
import static leo.lija.chess.Color.WHITE;


public class EloCalculator {

    private static final int P1WIN = -1;
    private static final int DRAW = 0;
    private static final int P2WIN = 1;

    public record User(int elo, int nbRatedGames) {}

    public Pair<Integer, Integer> calculate(User user1, User user2, Optional<Color> win) {
        int winCode = 0;
        if (win.isEmpty()) winCode = DRAW;
        else if (win.get() == WHITE) winCode = P1WIN;
        else if (win.get() == BLACK) winCode = P2WIN;
        return Pair.of(
            calculateUserElo(user1, user2.elo, -winCode),
            calculateUserElo(user2, user1.elo, winCode)
        );
    }

    private int calculateUserElo(User user, int opponentElo, int win) {
        float score = (1 + win) / 2f;
        double expected = 1 / (1 + Math.pow(10, (opponentElo - user.elo) / 400f));
        float kFactor = Math.round(user.nbRatedGames > 20 ? 16 : 50 - user.nbRatedGames * (34 / 20f));
        double diff = 2 * kFactor * (score - expected);

        return (int) Math.round(user.elo + diff);
    }
}
