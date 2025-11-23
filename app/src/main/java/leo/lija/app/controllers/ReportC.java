package leo.lija.app.controllers;

import leo.lija.app.reporting.Reporting;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReportC {

    private Reporting reporting;

    @GetMapping("/status")
    public String status() {
        return reporting.getStatus();
    }

    @GetMapping("/nb-players")
    public int nbPlayers() {
        return reporting.getNbMembers();
    }
}
