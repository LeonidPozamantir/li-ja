package leo.lija.system;

import leo.lija.chess.Game;
import leo.lija.chess.Move;
import leo.lija.chess.utils.Pair;
import leo.lija.system.db.EntryRepo;
import leo.lija.system.db.GameRepo;
import leo.lija.system.entities.DbGame;
import leo.lija.system.entities.Variant;
import leo.lija.system.entities.entry.Entry;
import leo.lija.system.exceptions.AppException;
import leo.lija.system.memo.EntryMemo;
import leo.lija.system.memo.VersionMemo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
public class Starter extends IOTools {

    private final EntryRepo entryRepo;
    private final EntryMemo entryMemo;
    private final Ai ai;

    public Starter(
            GameRepo gameRepo,
            EntryRepo entryRepo,
            VersionMemo versionMemo,
            EntryMemo entryMemo,
            Ai ai) {
        super(gameRepo, versionMemo);
        this.entryRepo = entryRepo;
        this.entryMemo = entryMemo;
        this.ai = ai;
    }

    public DbGame start(DbGame game, String entryData) {
        if (game.getVariant() != Variant.STANDARD) gameRepo.saveInitialFen(game);
        addEntry(game, entryData);
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

    private void addEntry(DbGame game, String data) {
        Entry.build(game, data).ifPresent(f -> {
            int id = entryMemo.increase();
            entryRepo.save(f.apply(id));
        });
    }
}
