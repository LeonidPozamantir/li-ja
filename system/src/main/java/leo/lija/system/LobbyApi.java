package leo.lija.system;

import leo.lija.chess.Color;
import leo.lija.system.db.GameRepo;
import leo.lija.system.db.HookRepo;
import leo.lija.system.entities.DbGame;
import leo.lija.system.memo.AliveMemo;
import leo.lija.system.memo.HookMemo;
import leo.lija.system.memo.LobbyMemo;
import leo.lija.system.memo.MessageMemo;
import leo.lija.system.memo.VersionMemo;
import org.springframework.stereotype.Service;

@Service
public class LobbyApi extends IOTools {

    private final HookRepo hookRepo;
    private final Messenger messenger;
    private final Starter starter;
    private final LobbyMemo lobbyMemo;
    private final MessageMemo messageMemo;
    private final AliveMemo aliveMemo;
    private final HookMemo hookMemo;

    LobbyApi(HookRepo hookRepo, Messenger messenger, Starter starter, LobbyMemo lobbyMemo, MessageMemo messageMemo, VersionMemo versionMemo, GameRepo gameRepo, AliveMemo aliveMemo, HookMemo hookMemo) {
        super(gameRepo, versionMemo);
        this.hookRepo = hookRepo;
        this.messenger = messenger;
        this.starter = starter;
        this.lobbyMemo = lobbyMemo;
        this.messageMemo = messageMemo;
        this.aliveMemo = aliveMemo;
        this.hookMemo = hookMemo;
    }

    public void join(String gameId, String colorName, String entryData, String messageString) {
        Color color = ioColor(colorName);
        DbGame game = gameRepo.game(gameId);
        messenger.systemMessages(game, messageString);
        starter.start(game, entryData);
        save(game);
        aliveMemo.put(gameId, color);
        aliveMemo.put(gameId, color.getOpposite());
        versionInc();
    }

    public void create(String hookOwnerId) {
        versionInc();
        hookMemo.put(hookOwnerId);
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
}
