package leo.lija.app.ai;

import leo.lija.chess.Game;
import leo.lija.chess.History;
import leo.lija.chess.Move;
import leo.lija.chess.Pos;
import leo.lija.chess.format.Fen;
import leo.lija.chess.utils.Pair;
import leo.lija.app.Ai;
import leo.lija.app.config.CraftyConfigProperties;
import leo.lija.app.entities.DbGame;
import leo.lija.app.entities.Variant;
import leo.lija.app.exceptions.AppException;
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

        Game oldGame = dbGame.toChess();
        String fen = Fen.obj2Str(dbGame.getVariant() == Variant.CHESS960
            ? oldGame.updateBoard(board -> board.updateHistory(History::withoutAnyCastles))
            : oldGame
        );

        String strMove = runCrafty(fen, dbGame.aiLevel().orElse(1));
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
        try {
            file.deleteOnExit();
        } catch (Exception e) {
            System.out.println("Error deleting crafty file on exit: " + e.getMessage());
        }
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
