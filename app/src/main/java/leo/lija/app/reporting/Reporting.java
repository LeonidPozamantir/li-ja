package leo.lija.app.reporting;

import leo.lija.app.db.GameRepo;
import leo.lija.app.game.HubMemo;
import leo.lija.app.site.Hub;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Reporting {

    private final Hub siteHub;
    private final GameRepo gameRepo;
    private final HubMemo gameHubMemo;

    @Getter
    private int nbMembers = 0;
    @Getter
    private long nbGames = 0;
    private long nbPlaying = 0;

    public String getStatus() {
        return status();
    }

    private String status() {
        return String.join(" ", String.valueOf(nbMembers), String.valueOf(nbGames), String.valueOf(nbPlaying));
    }

    public void update() {
        nbMembers = siteHub.getNbMembers();
        nbGames = gameRepo.countAll();
        nbPlaying = gameHubMemo.count();
    }
}
