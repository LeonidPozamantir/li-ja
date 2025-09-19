package leo.lija.app.controllers;

import leo.lija.system.LobbyXhr;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("lobby")
@RequiredArgsConstructor
public class LobbyXhrC {

    private final LobbyXhr xhr;

    @GetMapping("/sync")
    public void sync() {

    }
}
