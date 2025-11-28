package leo.lija.app.ai;

import leo.lija.app.config.CraftyConfigProperties;
import leo.lija.app.exceptions.AppException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CraftyServer {

    private final CraftyConfigProperties config;

    public String apply(String fen, int aiLevel) {

        if (aiLevel < 1 || aiLevel > 20) throw new AppException("Invalid ai level");
        if (fen.isEmpty()) throw new AppException("Empty fen");
        return runCrafty(fen, aiLevel);
    }

    @SneakyThrows
    private String runCrafty(String oldFen, int level) {
        ProcessBuilder builder = new ProcessBuilder("C:\\Windows\\System32\\cmd.exe", "/c", command());
        builder.redirectErrorStream(true);
        Process process = builder.start();

        ByteArrayInputStream inputStream = input(oldFen, level);
        try (OutputStream processInput = process.getOutputStream()) {
            inputStream.transferTo(processInput);
        }

        return extractMove(new BufferedReader(new InputStreamReader(process.getInputStream())));
    }

    private String extractMove(BufferedReader output) throws IOException {
        try (output) {
            return output.lines().filter(s -> s.startsWith("bestmove"))
                .findFirst().orElseThrow(() -> new AppException("Stockfish output does not contain bestmove"))
                .substring(9, 13);
        }
    }

    private String command() {
        return config.execPath();
    }

    private ByteArrayInputStream input(String fen, int level) {
        return new ByteArrayInputStream(List.of(
            "uci",
            "position fen %s".formatted(fen),
            "setoption name Skill Level value %d".formatted(craftySkill(level)),
            "go movetime %d".formatted(craftyTime(level)),
            "ucinewgame",
            "quit"
        ).stream().collect(Collectors.joining("\n")).getBytes(StandardCharsets.UTF_8));
    }

    private int craftyTime(int level) {
        return level * 100;
    }

    private int craftySkill(int level) {
        return (int) (level * 2.5);
    }

}
