package leo.lija.benchmark;

import leo.lija.chess.Color;
import leo.lija.chess.Game;
import leo.lija.chess.Pos;
import leo.lija.chess.utils.Pair;
import leo.lija.system.GameRepo;
import leo.lija.system.LijaApplication;
import leo.lija.system.Server;
import leo.lija.system.entities.DbGame;
import leo.lija.system.entities.DbPlayer;
import leo.lija.system.entities.Status;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static leo.lija.chess.Color.BLACK;
import static leo.lija.chess.Color.WHITE;
import static leo.lija.chess.Pos.A1;
import static leo.lija.chess.Pos.A2;
import static leo.lija.chess.Pos.A3;
import static leo.lija.chess.Pos.A5;
import static leo.lija.chess.Pos.A6;
import static leo.lija.chess.Pos.B1;
import static leo.lija.chess.Pos.B4;
import static leo.lija.chess.Pos.B7;
import static leo.lija.chess.Pos.B8;
import static leo.lija.chess.Pos.C1;
import static leo.lija.chess.Pos.C3;
import static leo.lija.chess.Pos.C6;
import static leo.lija.chess.Pos.C7;
import static leo.lija.chess.Pos.C8;
import static leo.lija.chess.Pos.D1;
import static leo.lija.chess.Pos.D2;
import static leo.lija.chess.Pos.D4;
import static leo.lija.chess.Pos.D5;
import static leo.lija.chess.Pos.D7;
import static leo.lija.chess.Pos.D8;
import static leo.lija.chess.Pos.E1;
import static leo.lija.chess.Pos.E2;
import static leo.lija.chess.Pos.E4;
import static leo.lija.chess.Pos.E6;
import static leo.lija.chess.Pos.E7;
import static leo.lija.chess.Pos.E8;
import static leo.lija.chess.Pos.F1;
import static leo.lija.chess.Pos.F3;
import static leo.lija.chess.Pos.F4;
import static leo.lija.chess.Pos.F8;
import static leo.lija.chess.Pos.G1;
import static leo.lija.chess.Pos.G4;
import static leo.lija.chess.Pos.H1;
import static leo.lija.chess.Pos.H2;
import static leo.lija.chess.Pos.H3;

@State(Scope.Benchmark)
@Fork(value = 1)
@Warmup(iterations = 1, time = 2)
@Measurement(iterations = 10, time = 10)
public class GameBenchmark {

    private static GameRepo repo;
    private static Server server;
    private static ConfigurableApplicationContext context;

    @Setup(Level.Trial)
    public static void setContext() {
        context = SpringApplication.run(LijaApplication.class);
        repo = context.getBean(GameRepo.class);
        server = context.getBean(Server.class);
    }

    @TearDown(Level.Trial)
    public static void close() {
        context.close();
    }

    @Benchmark
    public void timeChessImmortal() {
        playMoves(
            Pair.of(E2, E4),
            Pair.of(D7, D5),
            Pair.of(E4, D5),
            Pair.of(D8, D5),
            Pair.of(B1, C3),
            Pair.of(D5, A5),
            Pair.of(D2, D4),
            Pair.of(C7, C6),
            Pair.of(G1, F3),
            Pair.of(C8, G4),
            Pair.of(C1, F4),
            Pair.of(E7, E6),
            Pair.of(H2, H3),
            Pair.of(G4, F3),
            Pair.of(D1, F3),
            Pair.of(F8, B4),
            Pair.of(F1, E2),
            Pair.of(B8, D7),
            Pair.of(A2, A3),
            Pair.of(E8, C8),
            Pair.of(A3, B4),
            Pair.of(A5, A1),
            Pair.of(E1, D2),
            Pair.of(A1, H1),
            Pair.of(F3, C6),
            Pair.of(B7, C6),
            Pair.of(E2, A6)
        );
    }

    private Game playMove(Game game, Pos orig, Pos dest) {
        return game.apply(orig, dest).getFirst();
    }
    
    @SafeVarargs
    public final Game playMoves(Pair<Pos, Pos>... moves) {
        return Arrays.stream(moves)
            .reduce(new Game(), (g, move) -> {
                g.situation().destinations();
                return playMove(g, move.getFirst(), move.getSecond());
            }, (s1, s2) -> s1);
    }

    private Map<Pos, List<Pos>> move(DbGame game, String m) {
        return server.playMove(game.fullIdOf(WHITE), m);
    }

    List<String> moves = List.of("e2 e4", "d7 d5", "e4 d5", "d8 d5", "b1 c3", "d5 a5", "d2 d4", "c7 c6", "g1 f3", "c8 g4", "c1 f4", "e7 e6", "h2 h3", "g4 f3", "d1 f3", "f8 b4", "f1 e2", "b8 d7", "a2 a3", "e8 c8", "a3 b4", "a5 a1", "e1 d2", "a1 h1", "f3 c6", "b7 c6", "e2 a6");

    private List<Map<Pos, List<Pos>>> play(DbGame game) {
        return moves.stream().map(m -> move(game, m)).toList();
    }

    Random random = new Random();
    private String randomString(int len) {
        return Stream.generate(() -> String.valueOf(randomChar())).limit(len).collect(Collectors.joining());
    }
    private char randomChar() {
        return (char) (random.nextInt(25) + 97);
    }

    private DbPlayer newDbPlayer(String id, Color color, String ps) {
        return new DbPlayer(id, color, ps, null, null, "0s|1Msystem White creates the game|2Msystem Black joins the game", 1280);
    }


    @Benchmark
    public void timeSystemImmortal() {
        DbPlayer white = newDbPlayer(randomString(4), WHITE, "ip ar jp bn kp cb lp dq mp ek np fb op gn pp hr");
        DbPlayer black = newDbPlayer(randomString(4), BLACK, "Wp 4r Xp 5n Yp 6b Zp 7q 0p 8k 1p 9b 2p !n 3p ?r");
        DbGame game = new DbGame(
            randomString(8),
            white,
            black,
            "",
            Status.CREATED,
            0,
            Optional.empty(),
            Optional.empty()
        );
        repo.insert(game);
        play(game);
    }
}
