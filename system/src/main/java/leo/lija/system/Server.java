package leo.lija.system;

import leo.lija.chess.Game;
import leo.lija.chess.Move;
import leo.lija.chess.Pos;
import leo.lija.chess.Role;
import leo.lija.chess.utils.Pair;
import leo.lija.system.entities.DbGame;
import leo.lija.system.entities.event.EndEvent;
import leo.lija.system.exceptions.AppException;
import leo.lija.system.memo.VersionMemo;
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
    private final Ai ai;
    private final VersionMemo versionMemo;

    public Map<Pos, List<Pos>> playMove(String fullId, String moveString) {
        return playMove(fullId, moveString, Optional.empty());
    }

    public Map<Pos, List<Pos>> playMove(String fullId, String moveString, Optional<String> promString) {
        Pair<String, String> move =  decodeMoveString(moveString).orElseThrow(() -> new AppException("Wrong move"));
        return play(fullId, move.getFirst(), move.getSecond(), promString);
    }

    public Map<Pos, List<Pos>> play(String fullId, String fromString, String toString) {
        return play(fullId, fromString, toString, Optional.empty());
    }

    public Map<Pos, List<Pos>> play(String fullId, String fromString, String toString, Optional<String> promString) {
        DbGame game = repo.playerGame(fullId);
        purePlay(game, fromString, toString, promString);
        repo.save(game);
        versionMemo.put(game);
        return game.toChess().situation().destinations();
    }

    public void updateVersion(String gameId) {
        versionMemo.put(repo.game(gameId));
    }

    public void endGame(String gameId) {
        DbGame g1 = repo.game(gameId);
        g1.withEvents(List.of(new EndEvent()));
        repo.save(g1);
    }

    public void purePlay(DbGame game, String origString, String destString, Optional<String> promString) {
        if (!game.playable()) throw new AppException("Game is not playable");
        Pos orig = posAt(origString).orElseThrow(() -> new AppException("Wrong orig " + origString));
        Pos dest = posAt(destString).orElseThrow(() -> new AppException("Wrong dest " + destString));
        Role promotion = promString.map(ps -> Role.promotable(promString).orElseThrow(() -> new AppException("Wrong promotion " + promString))).orElse(null);
        Game chessGame = game.toChess();
        Pair<Game, Move> newChessGameAndMove = chessGame.apply(orig, dest, promotion);
        Game newChessGame = newChessGameAndMove.getFirst();
        Move move = newChessGameAndMove.getSecond();
        game.update(newChessGame, move);
        if (game.player().isAi()) aiResponse(game);
    }

    private void aiResponse(DbGame dbGame) {
        Pair<Game, Move> newChessGameAndMove = ai.apply(dbGame);
        Game newChessGame = newChessGameAndMove.getFirst();
        Move move = newChessGameAndMove.getSecond();
        dbGame.update(newChessGame, move);
    }



    private Optional<Pair<String, String>> decodeMoveString(String moveString) {
        Matcher matcher = MOVE_STRING.matcher(moveString);
        if (matcher.find()) {
            return Optional.of(Pair.of(matcher.group(1), matcher.group(2)));
        }
        return Optional.empty();
    }
}
