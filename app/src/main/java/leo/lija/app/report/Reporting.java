package leo.lija.app.report;

import leo.lija.app.Utils;
import leo.lija.app.ai.RemoteAi;
import leo.lija.app.db.GameRepo;
import leo.lija.app.game.HubMaster;
import leo.lija.app.site.Hub;
import leo.lija.chess.utils.Pair;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class Reporting {

    record SiteSocket(int nbMembers) {}
    record LobbySocket(int nbMembers) {}
    record GameSocket(int nbHubs, int nbMembers) {}

    private final TaskExecutor executor;

    private final Hub siteHub;
    private final leo.lija.app.lobby.Hub lobbyHub;
    private final HubMaster gameHubMaster;
    private final GameRepo gameRepo;
    private final RemoteAi remoteAiService;

    @Getter
    private long nbGames = 0;
    @Getter
    private long nbPlaying = 0;
    private float loadAvg = 0;
    private int nbThreads = 0;
    private long memory = 0;
    private long latency = 0;
    private SiteSocket site = new SiteSocket(0);
    private LobbySocket lobby = new LobbySocket(0);
    private GameSocket game = new GameSocket(0, 0);
    private boolean remoteAi = false;

    private int displays = 0;

    private OperatingSystemMXBean osStats = ManagementFactory.getOperatingSystemMXBean();
    private ThreadMXBean threadStats = ManagementFactory.getThreadMXBean();
    private MemoryMXBean memoryStats = ManagementFactory.getMemoryMXBean();

    public int getNbMembers() {
        return allMembers();
    }

    public String getStatus() {
        return status();
    }

    public void update() {
        long before = Utils.nowMillis();
        site = new SiteSocket(siteHub.getNbMembers());
        lobby = new LobbySocket(lobbyHub.getNbMembers());
        game = new GameSocket(gameHubMaster.getNbHubs(), gameHubMaster.getNbMembers());
        nbGames = gameRepo.countAll();
        nbPlaying = gameRepo.countPlaying();
        CompletableFuture.runAsync(() -> {
            latency = Utils.nowMillis() - before;
        });

        loadAvg = (float) osStats.getSystemLoadAverage();
        nbThreads = threadStats.getThreadCount();
        memory = memoryStats.getHeapMemoryUsage().getUsed() / 1024 / 1024;
        remoteAi = remoteAiService.currentHealth();

        display();
    }

    private void display() {
        Formatter.DataLine data = Formatter.dataLine(List.of(
            Pair.of("site", site.nbMembers),
            Pair.of("lobby", lobby.nbMembers),
            Pair.of("game", game.nbMembers),
            Pair.of("hubs", game.nbHubs),
            Pair.of("recent", nbPlaying),
            Pair.of("lat.", latency),
            Pair.of("thread", nbThreads),
            Pair.of("load", String.valueOf(loadAvg).replace("0.", ".")),
            Pair.of("mem", memory),
            Pair.of("AI", remoteAi ? "✔" : "●")
        ));

        if (displays % 8 == 0) {
            System.out.println(data.header());
        }
        displays++;
        System.out.println(data.line());
    }

    private String status() {
        return String.join(" ",
            String.valueOf(allMembers()),
            String.valueOf(nbGames),
            String.valueOf(nbPlaying),
            String.valueOf(game.nbHubs),
            String.valueOf(loadAvg),
            remoteAi ? "1" : "0"
        );
    }

    private int allMembers() {
        return site.nbMembers + lobby.nbMembers + game.nbMembers;
    }
}
