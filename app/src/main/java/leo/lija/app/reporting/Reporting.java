package leo.lija.app.reporting;

import leo.lija.app.ai.RemoteAi;
import leo.lija.app.db.GameRepo;
import leo.lija.app.site.Hub;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;

@Service
@RequiredArgsConstructor
public class Reporting {

    private final Hub siteHub;
    private final GameRepo gameRepo;
    private final RemoteAi remoteAiService;

    @Getter
    private int nbMembers = 0;
    @Getter
    private long nbGames = 0;
    @Getter
    private long nbPlaying = 0;
    private long nbGameSockets = 0;
    private float loadAvg = 0;
    private int nbThreads = 0;
    private boolean remoteAi = false;

    private OperatingSystemMXBean osStats = ManagementFactory.getOperatingSystemMXBean();
    private ThreadMXBean threadStats = ManagementFactory.getThreadMXBean();

    public String getStatus() {
        return status();
    }

    public void update() {
        nbMembers = siteHub.getNbMembers();
        nbGames = gameRepo.countAll();
        nbPlaying = gameRepo.countPlaying();
        nbGameSockets = 0; // gameHubMemo.count();
        loadAvg = 0; // (float) osStats.getSystemLoadAverage();
//        nbThreads = threadStats.getThreadCount();
        remoteAi = remoteAiService.currentHealth();
    }

    private String status() {
        return String.join(" ",
            String.valueOf(nbMembers),
            String.valueOf(nbGames),
            String.valueOf(nbPlaying),
            String.valueOf(nbGameSockets),
            String.valueOf(loadAvg),
            remoteAi ? "1" : "0"
        );
    }


}
