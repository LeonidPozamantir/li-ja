package leo.lija.app;

import leo.lija.chess.Game;
import leo.lija.chess.Move;
import leo.lija.chess.utils.Pair;
import leo.lija.app.db.EntryRepo;
import leo.lija.app.db.GameRepo;
import leo.lija.app.entities.DbGame;
import leo.lija.app.entities.Entry;
import leo.lija.app.entities.Variant;
import leo.lija.app.exceptions.AppException;
import leo.lija.app.memo.VersionMemo;
import org.springframework.stereotype.Service;

@Service
public class Starter extends IOTools {

    private final EntryRepo entryRepo;
    private final Ai ai;

    public Starter(
            GameRepo gameRepo,
            EntryRepo entryRepo,
            VersionMemo versionMemo,
            Ai ai) {
        super(gameRepo, versionMemo);
        this.entryRepo = entryRepo;
        this.ai = ai;
    }

    public DbGame start(DbGame game, String entryData) {
        if (game.getVariant() != Variant.STANDARD) gameRepo.saveInitialFen(game);
        Entry.apply(game, entryData).ifPresent(entryRepo::add);
        if (game.player().isAi()) {
            Pair<Game, Move> aiResult;
            try {
                aiResult = ai.apply(game);
            } catch (Exception e) {
                throw new AppException("AI failure");
            }
            Game newChessGame = aiResult.getFirst();
            Move move = aiResult.getSecond();
            game.update(newChessGame, move);
        }
        return game;
    }

}
