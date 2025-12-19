package leo.lija.app.controllers;

import io.vavr.Tuple3;
import leo.lija.app.Captcha;
import leo.lija.chess.Color;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/captcha")
@RequiredArgsConstructor
public class CaptchaC {

    private final Captcha captcha;

    @GetMapping("/create")
    public Map<String, String> create() {
        Tuple3<String, String, Color> data = captcha.create();
        return Map.of(
            "id", data._1,
            "fen", data._2,
            "color", data._3.toString()
        );
    }

    @GetMapping("/solve/{gameId}")
    public List<String> solve(@PathVariable String gameId) {
        return captcha.solve(gameId);
    }
}
