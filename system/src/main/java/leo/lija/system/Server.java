package leo.lija.system;

import leo.lija.chess.Game;
import leo.lija.chess.Move;
import leo.lija.chess.Pos;
import leo.lija.chess.Role;
import leo.lija.chess.utils.Pair;
import leo.lija.system.entities.DbGame;
import leo.lija.system.entities.DbPlayer;
import leo.lija.system.entities.EventStack;
import leo.lija.system.exceptions.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;

import static leo.lija.chess.Pos.posAt;
import static leo.lija.system.Utils.MOVE_STRING;

@Service
@RequiredArgsConstructor
public class Server {

    private final GameRepo repo;

    public Map<Pos, List<Pos>> playMove(String fullId, String moveString, Optional<String> promString) {
        return decodeMoveString(moveString)
            .map(moveParts -> {
                String origString = moveParts.getFirst();
                String destString = moveParts.getSecond();
                Pos orig = posAt(origString).orElseThrow(() -> new AppException("Wrong orig " + origString));
                Pos dest = posAt(destString).orElseThrow(() -> new AppException("Wrong dest " + destString));
                Role promotion = promString.map(ps -> Role.promotable(promString).orElseThrow(() -> new AppException("Wrong promotion " + promString))).orElse(null);
                Pair<DbGame, DbPlayer> gameAndPlayer = repo.player(fullId).orElseThrow(() -> new AppException("Wrong ID " + fullId));
                DbGame game = gameAndPlayer.getFirst();
                if (!game.playable()) throw new AppException("Game is not playable");
                Game chessGame = game.toChess();
                Pair<Game, Move> newChessGameAndMove = chessGame.apply(orig, dest, promotion);
                Game newChessGame = newChessGameAndMove.getFirst();
                Move move = newChessGameAndMove.getSecond();
                game.update(newChessGame, move);
                repo.save(game);
                return newChessGame.situation().destinations();
            })
            .orElseThrow(() -> new AppException("Wrong move"));
    }

    public Map<Pos, List<Pos>> playMove(String fullId, String moveString) {
        return playMove(fullId, moveString, Optional.empty());
    }

    private Map<DbPlayer, EventStack> moveToEvents(Move move) {
        return Map.of();
    }

    private Optional<Pair<String, String>> decodeMoveString(String moveString) {
        Matcher matcher = MOVE_STRING.matcher(moveString);
        if (matcher.find()) {
            return Optional.of(Pair.of(matcher.group(1), matcher.group(2)));
        }
        return Optional.empty();
    }
}
