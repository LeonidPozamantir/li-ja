package leo.lija.app;

import io.vavr.Tuple3;
import leo.lija.app.db.GameRepo;
import leo.lija.app.entities.DbGame;
import leo.lija.app.exceptions.AppException;
import leo.lija.chess.Color;
import leo.lija.chess.Game;
import leo.lija.chess.Move;
import leo.lija.chess.format.Fen;
import leo.lija.chess.format.pgn.PgnReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class Captcha {

    private final GameRepo gameRepo;

    // returns game id and game fen and current player color
    public Tuple3<String, String, Color> create() {
        return gameRepo.findOneStandardCheckmate()
            .map(game -> {
                Game rewinded = rewind(game);
                return new Tuple3<>(game.getId(), fen(rewinded), rewinded.getPlayer());
            }).orElseThrow(() -> new AppException("No checkmate available"));
    }

    public List<String> solve(String id) {
        return gameRepo.game(id).map(game -> {
                Game rewinded = rewind(game);
                List<String> moves = mateMoves(rewinded);
                if (!moves.isEmpty()) return moves;
                throw new AppException("No solution found");
            })
            .orElseThrow(() -> new AppException("No such game"));
    }

    private List<String> mateMoves(Game game) {
        return game.situation().moves().values().stream()
            .flatMap(moves -> moves.stream().filter(move ->
                move.after().situationOf(game.getPlayer().getOpposite()).checkmate()
            ))
            .map(Move::notation)
            .toList();
    }

    private Game rewind(DbGame game) {
        try {
            return PgnReader.withSans(game.getPgn(), l -> l.subList(0, l.size() - 1))
                .game();
        } catch (AppException e) {
            throw failInfo(game, e);
        }
    }

    private String fen(Game game) {
        String f = Fen.obj2Str(game);
        int idx = f.indexOf(' ');
        return idx == -1 ? f : f.substring(0, idx);
    }

    private AppException failInfo(DbGame game, Exception e) {
        return new AppException("Rewind %s".formatted(game.getId()), e);
    }
}
