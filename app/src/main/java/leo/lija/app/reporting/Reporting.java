package leo.lija.app.reporting;

import leo.lija.app.ai.RemoteAi;
import leo.lija.app.db.GameRepo;
import leo.lija.app.game.HubMemo;
import leo.lija.app.site.Hub;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class Reporting {

    private final Hub siteHub;
    private final GameRepo gameRepo;
    private final HubMemo gameHubMemo;
    private final RemoteAi remoteAiService;

    @Getter
    private int nbMembers = 0;
    @Getter
    private long nbGames = 0;
    @Getter
    private long nbPlaying = 0;
    private long nbGameSockets = 0;
    private Optional<Float> loadAvg = Optional.empty();
    private boolean remoteAi = false;

    private OperatingSystemMXBean osStats = ManagementFactory.getOperatingSystemMXBean();

    public String getStatus() {
        return status();
    }

    public void update() {
        nbMembers = siteHub.getNbMembers();
        nbGames = gameRepo.countAll();
        nbPlaying = gameRepo.countPlaying();
        nbGameSockets = gameHubMemo.count();
        loadAvg = getLoadAvg();
        remoteAi = remoteAiService.currentHealth();
    }

    private Optional<Float> getLoadAvg() {
        return Optional.of((float) osStats.getSystemLoadAverage());
    }

    private String status() {
        return String.join(" ",
            String.valueOf(nbMembers),
            String.valueOf(nbGames),
            String.valueOf(nbPlaying),
            String.valueOf(nbGameSockets),
            loadAvg.map(String::valueOf).orElse("?"),
            remoteAi ? "1" : "0"
        );
    }


}
