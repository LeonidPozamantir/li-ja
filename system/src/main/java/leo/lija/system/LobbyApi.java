package leo.lija.system;

import leo.lija.chess.Color;
import leo.lija.system.db.EntryRepo;
import leo.lija.system.db.GameRepo;
import leo.lija.system.db.HookRepo;
import leo.lija.system.entities.DbGame;
import leo.lija.system.entities.entry.Entry;
import leo.lija.system.memo.AliveMemo;
import leo.lija.system.memo.EntryMemo;
import leo.lija.system.memo.HookMemo;
import leo.lija.system.memo.LobbyMemo;
import leo.lija.system.memo.MessageMemo;
import leo.lija.system.memo.VersionMemo;
import org.springframework.stereotype.Service;

@Service
public class LobbyApi extends IOTools {

    private final HookRepo hookRepo;
    private final EntryRepo entryRepo;
    private final Messenger messenger;
    private final LobbyMemo lobbyMemo;
    private final MessageMemo messageMemo;
    private final EntryMemo entryMemo;
    private final AliveMemo aliveMemo;
    private final HookMemo hookMemo;

    LobbyApi(HookRepo hookRepo, EntryRepo entryRepo, Messenger messenger, LobbyMemo lobbyMemo, MessageMemo messageMemo, EntryMemo entryMemo, VersionMemo versionMemo, GameRepo gameRepo, AliveMemo aliveMemo, HookMemo hookMemo) {
        super(gameRepo, versionMemo);
        this.hookRepo = hookRepo;
        this.entryRepo = entryRepo;
        this.messenger = messenger;
        this.lobbyMemo = lobbyMemo;
        this.messageMemo = messageMemo;
        this.entryMemo = entryMemo;
        this.aliveMemo = aliveMemo;
        this.hookMemo = hookMemo;
    }

    public void join(String gameId, String colorName, String entryData, String messageString) {
        Color color = ioColor(colorName);
        DbGame game = gameRepo.game(gameId);
        messenger.systemMessages(game, messageString);
        save(game);
        aliveMemo.put(gameId, color);
        aliveMemo.put(gameId, color.getOpposite());
        versionInc();
        addEntry(game, entryData);
    }

    public void create(String hookOwnerId) {
        versionInc();
        hookMemo.put(hookOwnerId);
    }

    public void remove(String hookId) {
        hookRepo.deleteById(hookId);
        versionInc();
    }

    public void alive(String hookOwnerId) {
        hookMemo.put(hookOwnerId);
    }

    public void messageRefresh() {
        messageMemo.refresh();
    }

    private int versionInc() {
        return lobbyMemo.increase();
    }

    public void addEntry(DbGame game, String data) {
        Entry.build(game, data).ifPresent(f -> {
            int id = entryMemo.increase();
            entryRepo.save(f.apply(id));
        });
    }
}
