package leo.lija.app;

import leo.lija.app.ai.AiService;
import leo.lija.app.db.EntryRepo;
import leo.lija.app.db.GameRepo;
import leo.lija.app.entities.DbGame;
import leo.lija.app.entities.Entry;
import leo.lija.app.entities.Progress;
import leo.lija.app.entities.Variant;
import leo.lija.app.exceptions.AppException;
import leo.lija.app.lobby.Socket;
import leo.lija.chess.Game;
import leo.lija.chess.Move;
import leo.lija.chess.utils.Pair;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Starter {

    private final GameRepo gameRepo;
    private final EntryRepo entryRepo;
    private final Socket lobbySocket;
    private final AiService ai;


    public Progress start(DbGame game, String entryData) {
        if (game.getVariant() != Variant.STANDARD) gameRepo.saveInitialFen(game);
        Entry.apply(game, entryData).ifPresent(entry -> {
            entryRepo.add(entry);
            lobbySocket.addEntry(entry);
        });
        if (game.player().isAi()) {
            Pair<Game, Move> aiResult;
            try {
                aiResult = ai.apply(game);
            } catch (Exception e) {
                throw new AppException("AI failure");
            }
            Game newChessGame = aiResult.getFirst();
            Move move = aiResult.getSecond();
            return game.update(newChessGame, move);
        }
        return new Progress(game);
    }

}
