package leo.lija.app.ai;

import leo.lija.chess.Game;
import leo.lija.chess.Move;
import leo.lija.chess.utils.Pair;
import leo.lija.app.Ai;
import leo.lija.app.entities.DbGame;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
public class RemoteAi extends FenBased implements Ai {
    
    private final RestClient restClient;

    // indicates whether the remote AI is healthy
    // frequently updated by a cron
    private boolean health = false;

    public RemoteAi(String remoteUrl) {
        restClient = RestClient.builder()
            .baseUrl(remoteUrl)
            .build();
    }

    public Pair<Game, Move> apply(DbGame dbGame) {

        Game oldGame = dbGame.toChess();
        String fen = toFen(oldGame, dbGame.getVariant());

        String strMove = fetchMove(fen, dbGame.aiLevel().orElse(1));
        return applyFen(oldGame, strMove);
    }

    public Ai or(Ai fallback) {
        return health ? this : fallback;
    }

    public boolean currentHealth() {
        return health;
    }

    public void diagnose() {
        boolean h = healthCheck();
        if (h) {
            if (!health) System.out.println("remote AI is up");
        } else System.out.println("remote AI is down");
        health = h;
    }

    private String fetchMove(String oldFen, int level) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("fen", oldFen)
                        .queryParam("level", level)
                        .build())
                .retrieve()
                .body(String.class);
    }

    private boolean healthCheck() {
        try {
            fetchMove("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq", 1);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
