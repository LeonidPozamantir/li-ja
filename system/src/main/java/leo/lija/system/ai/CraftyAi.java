package leo.lija.system.ai;

import leo.lija.chess.Game;
import leo.lija.chess.History;
import leo.lija.chess.Move;
import leo.lija.chess.Pos;
import leo.lija.chess.format.Fen;
import leo.lija.chess.utils.Pair;
import leo.lija.system.Ai;
import leo.lija.system.config.CraftyConfigProperties;
import leo.lija.system.entities.DbGame;
import leo.lija.system.entities.Variant;
import leo.lija.system.exceptions.AppException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class CraftyAi implements Ai {

    private final CraftyConfigProperties config;

    @Override
    public Pair<Game, Move> apply(DbGame dbGame) {

        Game oldGame;
        if (dbGame.getVariant() == Variant.STANDARD) oldGame = dbGame.toChess();
        else if (dbGame.getVariant() == Variant.CHESS960) oldGame = dbGame.toChess().updateBoard(board ->
                board.updateHistory(History::withoutAnyCastles));
        else {
            oldGame = null;
        }

        String strMove = runCrafty(Fen.obj2Str(oldGame), dbGame.aiLevel().orElse(1));
        Pos orig = Pos.posAt(strMove.substring(0, 2)).get();
        Pos dest = Pos.posAt(strMove.substring(2, 4)).get();
        return oldGame.apply(orig, dest);
    }

    @SneakyThrows
    private String runCrafty(String oldFen, int level) {
        File file = writeFile("lichess_crafty_", input(oldFen, level));
        ProcessBuilder builder = new ProcessBuilder("C:\\Windows\\System32\\cmd.exe", "/c", command() + " < " + file.getAbsolutePath());
        builder.redirectErrorStream(true);
        Process process = builder.start();
        return extractMove(new BufferedReader(new InputStreamReader(process.getInputStream())));
    }

    private String extractMove(BufferedReader output) {
        return output.lines().filter(s -> s.startsWith("bestmove"))
            .findFirst().orElseThrow(() -> new AppException("Stockfish output does not contain bestmove"))
            .substring(9, 13);
    }

    private String command() {
        return config.execPath();
    }

    private List<String> input(String fen, int level) {
        return List.of(
            "uci",
            "position fen %s".formatted(fen),
            "setoption name Skill Level value %d".formatted(craftySkill(level)),
            "go movetime %d".formatted(craftyTime(level)),
            "ucinewgame",
            "quit"
        );
    }

    private int craftyTime(int level) {
        return level * 100;
    }

    private int craftySkill(int level) {
        return (int) (level * 2.5);
    }

    @SneakyThrows
    private File writeFile(String prefix, List<String> data) {
        File file = File.createTempFile(prefix, ".tmp");
        file.deleteOnExit();
        printToFile(file, p -> data.forEach(p::println));
        return file;
    }

    @SneakyThrows
    private void printToFile(File f, Consumer<PrintWriter> op) {
        PrintWriter p = new PrintWriter(f);
        try {
            op.accept(p);
        } finally {
            p.close();
        }

    }
}
