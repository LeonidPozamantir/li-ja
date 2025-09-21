package leo.lija.system;

import leo.lija.chess.Color;
import leo.lija.system.db.EntryRepo;
import leo.lija.system.db.GameRepo;
import leo.lija.system.db.HookRepo;
import leo.lija.system.entities.DbGame;
import leo.lija.system.entities.entry.Entry;
import leo.lija.system.entities.entry.EntryGame;
import leo.lija.system.memo.AliveMemo;
import leo.lija.system.memo.EntryMemo;
import leo.lija.system.memo.HookMemo;
import leo.lija.system.memo.LobbyMemo;
import leo.lija.system.memo.VersionMemo;
import org.springframework.stereotype.Service;

@Service
public class LobbyApi extends IOTools {

    private final HookRepo hookRepo;
    private final EntryRepo entryRepo;
    private final LobbyMemo lobbyMemo;
    private final EntryMemo entryMemo;
    private final AliveMemo aliveMemo;
    private final HookMemo hookMemo;

    LobbyApi(HookRepo hookRepo, EntryRepo entryRepo, LobbyMemo lobbyMemo, EntryMemo entryMemo, VersionMemo versionMemo, GameRepo gameRepo, AliveMemo aliveMemo, HookMemo hookMemo) {
        super(gameRepo, versionMemo);
        this.hookRepo = hookRepo;
        this.entryRepo = entryRepo;
        this.lobbyMemo = lobbyMemo;
        this.entryMemo = entryMemo;
        this.aliveMemo = aliveMemo;
        this.hookMemo = hookMemo;
    }

    public void join(String gameId, String colorName, EntryGame entryGame) {
        Color color = ioColor(colorName);
        DbGame g1 = gameRepo.game(gameId);
        aliveMemo.put(gameId, color);
        aliveMemo.put(gameId, color.getOpposite());
        versionInc();
        addEntry(entryGame);
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

    private int versionInc() {
        return lobbyMemo.increase();
    }

    public void addEntry(EntryGame entryGame) {
        int nextId = entryMemo.increase();
        entryRepo.save(new Entry(nextId, entryGame));
    }
}
