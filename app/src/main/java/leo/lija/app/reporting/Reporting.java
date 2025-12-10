package leo.lija.app.reporting;

import leo.lija.app.ai.RemoteAi;
import leo.lija.app.db.GameRepo;
import leo.lija.app.game.HubMaster;
import leo.lija.app.site.Hub;
import leo.lija.chess.utils.Pair;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class Reporting {

    record SiteSocket(int nbMembers) {}
    record LobbySocket(int nbMembers) {}
    record GameSocket(int nbHubs, int nbMembers) {}

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
    private SiteSocket site = new SiteSocket(0);
    private LobbySocket lobby = new LobbySocket(0);
    private GameSocket game = new GameSocket(0, 0);
    private boolean remoteAi = false;

    private int displays = 0;

    private OperatingSystemMXBean osStats = ManagementFactory.getOperatingSystemMXBean();
    private ThreadMXBean threadStats = ManagementFactory.getThreadMXBean();
    private MemoryMXBean memoryStats = ManagementFactory.getMemoryMXBean();

    public int getNbMembers() {
        return site.nbMembers;
    }

    public String getStatus() {
        return status();
    }

    public void update() {
        site = new SiteSocket(siteHub.getNbMembers());
        lobby = new LobbySocket(lobbyHub.getNbMembers());
        game = new GameSocket(gameHubMaster.getNbHubs(), gameHubMaster.getNbMembers());
        nbGames = gameRepo.countAll();
        nbPlaying = gameRepo.countPlaying();
        loadAvg = (float) osStats.getSystemLoadAverage();
        nbThreads = threadStats.getThreadCount();
        memory = memoryStats.getHeapMemoryUsage().getUsed() / 1024 / 1024;
        remoteAi = remoteAiService.currentHealth();

        display();
    }

    private void display() {
        List<Pair<String, Object>> data = List.of(
            Pair.of("site", site.nbMembers),
            Pair.of("lobby", lobby.nbMembers),
            Pair.of("game", game.nbMembers),
            Pair.of("hubs", game.nbHubs),
            Pair.of("threads", nbThreads),
            Pair.of("load", loadAvg),
            Pair.of("memory", memory)
        );

        if (displays % 10 == 0) {
            System.out.println(data.stream().map(Pair::getSecond).map(Object::toString).collect(Collectors.joining(" ")));
        }
        displays++;
        data.forEach(p -> {
            String name = p.getFirst();
            Object value = p.getSecond();
            String s = value.toString();
            System.out.print(" ".repeat(Math.max(name.length() - s.length(), 0)) + s + " ");
        });
        System.out.println();
    }

    private String status() {
        return String.join(" ",
            String.valueOf(site.nbMembers),
            String.valueOf(nbGames),
            String.valueOf(nbPlaying),
            String.valueOf(game.nbHubs),
            String.valueOf(loadAvg),
            remoteAi ? "1" : "0"
        );
    }


}
