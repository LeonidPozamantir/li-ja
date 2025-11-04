package leo.lija.system.ai;

import leo.lija.chess.Game;
import leo.lija.chess.Move;
import leo.lija.chess.utils.Pair;
import leo.lija.system.Ai;
import leo.lija.system.entities.DbGame;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
public class RemoteAi extends FenBased implements Ai {
    
    RestClient restClient;

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

    private String fetchMove(String oldFen, int level) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("fen", oldFen)
                        .queryParam("level", level)
                        .build())
                .retrieve()
                .body(String.class);
    }

    public boolean health() {
        try {
            fetchMove("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq", 1);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
