package leo.lija.system;

import leo.lija.chess.Board;
import leo.lija.chess.Game;
import leo.lija.chess.Move;
import leo.lija.chess.Piece;
import leo.lija.chess.Pos;
import leo.lija.chess.Role;
import leo.lija.chess.utils.Pair;
import leo.lija.system.entities.DbClock;
import leo.lija.system.entities.DbGame;
import leo.lija.system.entities.DbPlayer;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static leo.lija.chess.Color.WHITE;
import static leo.lija.chess.Pos.D2;
import static leo.lija.chess.Pos.D4;
import static leo.lija.system.entities.DbGame.GAME_ID_SIZE;
import static leo.lija.system.entities.DbGame.PLAYER_ID_SIZE;

public class Fixtures {

    Random random = new Random();
    private String randomString(int len) {
        return Stream.generate(() -> String.valueOf(randomChar())).limit(len).collect(Collectors.joining());
    }
    private char randomChar() {
        return (char) (random.nextInt(25) + 97);
    }

    DbPlayer white = newDbPlayer("white", "ip ar jp bn kp cb lp dq mp ek np fb op gn pp hr");
    DbPlayer black = newDbPlayer("black", "Wp 4r Xp 5n Yp 6b Zp 7q 0p 8k 1p 9b 2p !n 3p ?r");

    DbGame newDbGame = new DbGame(
        "arstdhne",
        List.of(white, black),
        "",
        10,
        0,
        null,
        null
    );

    public DbGame newDbGameWithBoard(Board b) {
        DbGame g = newDbGame.copy();
        g.update(new Game(b), anyMove);
        return g;
    }

    public DbGame newDbGameWithRandomIds() {
        List<DbPlayer> players = newDbGame.getPlayers().stream().map(p -> new DbPlayer(randomString(PLAYER_ID_SIZE), p.getColor(), p.getPs(), p.getAiLevel(), p.getIsWinner(),
            p.getEvts(), p.getElo())).toList();
        return new DbGame(randomString(GAME_ID_SIZE), players, newDbGame.getPgn(), newDbGame.getStatus(), newDbGame.getTurns(), newDbGame.getClock(), newDbGame.getLastMove());
    }

    DbPlayer newDbPlayer(String color, String ps) {
        return newDbPlayer(color, ps, "0s|1Msystem White creates the game|2Msystem Black joins the game");
    }

    DbPlayer newDbPlayer(String color, String ps, String evts) {
        return new DbPlayer(
            color.substring(0, 4),
            color,
            ps,
            null,
            null,
            evts,
            1280
        );
    }

    DbGame dbGame1 = new DbGame(
        "huhuhaha",
        List.of(
            newDbPlayer("white", "ip ar sp16 sN14 kp ub8 Bp6 dq Kp0 ek np LB12 wp22 Fn2 pp hR"),
            newDbPlayer("black", "Wp 4r Xp Qn1 Yp LB13 Rp9 hq17 0p 8k 1p 9b 2p sN3 3p ?r")
        ),
        "e4 Nc6 Nf3 Nf6 e5 Ne4 d3 Nc5 Be3 d6 d4 Ne4 Bd3 Bf5 Nc3 Nxc3 bxc3 Qd7 Bxf5 Qxf5 Nh4 Qe4 g3 Qxh1+",
        31,
        24,
        null,
        null
    );

    DbGame dbGame2 = new DbGame(
        "-176b4to",
        List.of(
            newDbPlayer("white", "zP32 Yr44 jp JN10 Jp20 cb Kp18 KQ2 KP0 Gk30 np ZB22 op QN4 pp dr50"),
            newDbPlayer("black", "WP Ar19 BP13 QN11 YP ZB35 KP21 KQ3 KP1 Ik37 1p zB29 Ep5 JN9 3p 5r25")
        ),
        "e4 e5 Qh5 Qf6 Nf3 g6 Qxe5+ Qxe5 Nxe5 Nf6 Nc3 Nc6 Nxc6 bxc6 e5 Nd5 Nxd5 cxd5 d4 Rb8 c3 d6 Be2 dxe5 dxe5 Rg8 Bf3 d4 cxd4 Bb4+ Ke2 g5 a3 g4 Bc6+ Bd7 Bxd7+ Kxd7 axb4 Rxb4 Kd3 Rb3+ Kc4 Rb6 Rxa7 Rc6+ Kb5 Rb8+ Ka5 Rc4 Rd1 Kc6 d5+ Kc5 Rxc7#",
        30,
        55,
        new DbClock(
            "black",
            5,
            1200,
            Map.of("white", 196.25f, "black", 304.1f)
        ),
        "a7 c7"
    );

    DbGame dbGame3 = new DbGame(
        "-7xfxoj4v",
        List.of(
            newDbPlayer("white", "zb6 dB 6Q12 uN4 DN18 4r76 kk24 3r42 rp68 rP64 sP22 PP0 tp78 vp2 LP8 MP30"),
            newDbPlayer("black", "LB3 PB9 6Q51 IN11 sN5 PR41 7k55 MR17 qP37 zP59 rP7 tP1 DP23 Dp13 Ep49 NP15")
        ),
        "d4 d5 f3 Bf5 Ne3 Nd6 Bd2 c6 g4 Bb6 gxf5 Nd7 Qg5 f6 Qg4 h5 Qh4 Rh6 N1g2 Rg6 Qf2 Rg5 c3 e6 Kc1 exf5 Kb1 f4 Nxf4 Nf5 h4 Nxe3 hxg5 Nxd1 Qf1 Nxc3+ Bxc3 a5 Qf2 Nc5 Kc1 Ra6 Rh2 fxg5 dxc5 gxf4 cxb6 Rxb6 Rxh5 g6 Qxb6 Qe6 Qd8+ Qc8 Qxc8+ Kxc8 Rh2 a4 Kc2 b5 Rh7 c5 Bg7 a3 b3 c4 Bf6 cxb3+ axb3 b4 Be5 g5 Bd6 Kd8 Bxb4 d4 Rxa3 d3+ exd3 g4 Ra8#",
        30,
        81,
        null,
        "a3 a8"
    );

    DbGame dbGame4 = new DbGame(
        "-huhuhiha",
        List.of(
            newDbPlayer("white", "ip ar sp sN kp ub Bp dq Kp ek np LB wp Fn pp hR"),
            newDbPlayer("black", "Wp 4r Xp Qn Yp LB Rp hq 0p 8k 1p 9b 2p sN 3p ?r")
        ),
        "e4 Nc6 Nf3 Nf6 e5 Ne4 d3 Nc5 Be3 d6 d4 Ne4 Bd3 Bf5 Nc3 Nxc3 bxc3 Qd7 Bxf5 Qxf5 Nh4 Qe4 g3 Qxh1+",
        31,
        24,
        null,
        null
    );

    DbGame dbGame5 = new DbGame(
        "luik7l0a",
        List.of(
            newDbPlayer("white", "qp4 aR rp0 bn sp46 TB2 uP26 dQ uP14 kk40 MP22 QB8 EP6 gn 3P10 lr44", "109p|110m1vb|111p|112mplw|113p|114mvub|115p|116ml7w|117C?|118p|119m?3b|120p|121m7lw|122Msystem Time out|123e|124p"),
            newDbPlayer("black", "Op5 7R47 Xp QN21 Ip19 Qb17 uP1 dQ39 KP3 3k15 EP9 ab13 Up33 TN11 Mp7 ur15", "109p|110m1vb|111p|112mplw|113p|114mvub|115p|116ml7w|117C?|118p|119m?3b|120p|121m7lw|122Msystem Time out|123e|124p")
        ),
        "b3 d5 Bb2 e6 a3 a6 g3 h6 Bg2 f5 h3 Nf6 Bf1 Be7 e3 O-O Bg2 Bd7 h4 c5 h5 Nc6 f3 d4 g4 dxe3 dxe3 fxg4 fxg4 e5 g5 hxg5 h6 g6 h7+ Kh8 Bxc6 Bxc6 Bxe5 Qxd1+ Kxd1 Rf7 Bxf6+ Bxf6 Rh2 Bxa1 c3 Rd8+ Kc2 Rf3 Rd2 Rxe3 Rxd8+ Kxh7 Rd2",
        35,
        55,
        new DbClock(
            "black",
            0,
            60,
            Map.of("white", 27.61f, "black", 60.24f)
        ),
        "d8 d2"
    );

    public Move newMove(Piece piece, Pos orig, Pos dest) {
        return newMove(piece, orig, dest, Optional.empty());
    }

    public Move newMove(Piece piece, Pos orig, Pos dest, Optional<Pos> capture) {
        return newMove(piece, orig, dest, capture, false);
    }

    public Move newMoveWithPromotion(Piece piece, Pos orig, Pos dest, Optional<Role> promotion) {
        return new Move(piece, orig, dest, new Board(), new Board(), Optional.empty(), promotion, Optional.empty(), false);
    }

    public Move newMoveWithCastle(Piece piece, Pos orig, Pos dest, Optional<Pair<Pos, Pos>> castle) {
        return new Move(piece, orig, dest, new Board(), new Board(), Optional.empty(), Optional.empty(), castle, false);
    }

    public Move newMove(Piece piece, Pos orig, Pos dest, Optional<Pos> capture, boolean enpassant) {
        return new Move(piece, orig, dest, new Board(), new Board(), capture, Optional.empty(), Optional.empty(), enpassant);
    }

    public final Move anyMove = newMove(WHITE.pawn(), D2, D4);
}
