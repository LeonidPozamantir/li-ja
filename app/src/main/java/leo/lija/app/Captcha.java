package leo.lija.app;

import io.vavr.Tuple3;
import leo.lija.app.db.GameRepo;
import leo.lija.app.exceptions.AppException;
import leo.lija.chess.Board;
import leo.lija.chess.Color;
import leo.lija.chess.Game;
import leo.lija.chess.Move;
import leo.lija.chess.Pos;
import leo.lija.chess.format.Fen;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class Captcha {

    private final GameRepo gameRepo;

    // returns game id and game fen and current player color
    public Tuple3<String, String, Color> create() {
        return gameRepo.findOneCheckmate()
            .map(game -> {
                Game rewinded = rewind(game.toChess());
                return new Tuple3<>(game.getId(), fen(rewinded), rewinded.getPlayer());
            }).orElseThrow(() -> new AppException("No checkmate available"));
    }

    public List<String> solve(String id) {
        return gameRepo.game(id).map(game -> {
                Game rewinded = rewind(game.toChess());
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

    private Game rewind(Game game) {
        return game.getBoard().getHistory().lastMove()
            .map(lastMove -> {
                Pos orig = lastMove.getFirst();
                Pos dest = lastMove.getSecond();
                Board rewindedBoard = game.getBoard().move(dest, orig).orElseThrow(() -> new AppException("Can't rewind board"));
                Game g2 = game.withBoard(rewindedBoard);
                Game g3 = g2.withPlayer(game.getPlayer().getOpposite());
                Game g4 = g3.withTurns(game.getTurns() - 1);
                return g4;
            }).orElseThrow(() -> new AppException("No last move"));
    }

    private String fen(Game game) {
        String f = Fen.obj2Str(game);
        int idx = f.indexOf(' ');
        return idx == -1 ? f : f.substring(0, idx);
    }

}
