package leo.lija.chess.format.pgn;

import leo.lija.chess.Game;
import leo.lija.chess.Move;
import leo.lija.chess.Replay;
import leo.lija.chess.exceptions.ChessException;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class PgnReader {

    public Replay apply(String pgn) {
        ParsedPgn parsed  = PgnParser.apply(pgn);
        return parsed.sans().stream().reduce(Replay.apply(makeGame(parsed.tags())),
            (replay, san) -> {
                Move move = san.apply(replay.game());
                List<Move> moves = replay.moves();
                moves.add(move);
                return new Replay(replay.game().apply(move), moves);
            },
            (r1, r2) -> r1
        );
    }

    public Game makeGame(List<Tag> tags) {
        List<String> fens = tags.stream()
            .filter(t -> t instanceof Fen)
            .map(t -> t.value)
            .toList();
        if (fens.isEmpty()) return new Game();
        if (fens.size() > 1) throw new ChessException("Multiple fen tags");
        String fen = fens.getFirst();
        return leo.lija.chess.format.Fen.str2Obj(fen)
            .map(situation -> new Game(situation.getBoard(), situation.getColor()))
            .orElseThrow(() -> new ChessException("Invalid fen %s".formatted(fen)));
    }
}
