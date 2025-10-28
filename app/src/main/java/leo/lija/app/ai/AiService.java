package leo.lija.app.ai;

import leo.lija.chess.Game;
import leo.lija.chess.Move;
import leo.lija.chess.utils.Pair;
import leo.lija.app.Ai;
import leo.lija.app.entities.DbGame;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiService {

    private final StupidAi stupidAi;
    private final CraftyAi craftyAi;
    private final RemoteAi remoteAi;

    @Value("${ai.use}")
    private String aiUse;

    @Setter
    private boolean remoteAiHealth = false;

    public Pair<Game, Move> apply(DbGame dbGame) {
        Ai ai;
        if (aiUse.equals("remote")) {
            ai = remoteAiHealth ? remoteAi : craftyAi;
        } else if (aiUse.equals("crafty")) ai = craftyAi;
        else ai = stupidAi;
        return ai.apply(dbGame);
    }
}
