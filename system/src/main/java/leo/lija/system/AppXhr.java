package leo.lija.system;

import leo.lija.chess.Game;
import leo.lija.chess.Move;
import leo.lija.chess.Pos;
import leo.lija.chess.Role;
import leo.lija.chess.utils.Pair;
import leo.lija.system.db.GameRepo;
import leo.lija.system.entities.DbGame;
import leo.lija.system.entities.DbPlayer;
import leo.lija.system.exceptions.AppException;
import leo.lija.system.memo.AliveMemo;
import leo.lija.system.memo.VersionMemo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Matcher;

import static leo.lija.chess.Pos.posAt;
import static leo.lija.system.Utils.MOVE_STRING;

@Service
@RequiredArgsConstructor
public class AppXhr {

    private final GameRepo gameRepo;
    private final Ai ai;
    private final VersionMemo versionMemo;
    private final AliveMemo aliveMemo;

    public void playMove(String fullId, String moveString) {
        playMove(fullId, moveString, Optional.empty());
    }

    public void playMove(String fullId, String moveString, Optional<String> promString) {
        Pair<String, String> move =  decodeMoveString(moveString).orElseThrow(() -> new AppException("Wrong move"));
        play(fullId, move.getFirst(), move.getSecond(), promString);
    }

    public void play(String fullId, String fromString, String toString) {
        play(fullId, fromString, toString, Optional.empty());
    }

    public void play(String fullId, String fromString, String toString, Optional<String> promString) {
        Pair<DbGame, DbPlayer> gp = gameRepo.player(fullId);
        DbGame g1 = gp.getFirst();
        DbPlayer player = gp.getSecond();
        purePlay(g1, fromString, toString, promString);
        if (g1.player().isAi()) {
            Pair<Game, Move> aiResult;
            try {
                aiResult = ai.apply(g1);
            } catch (Exception e) {
                throw new AppException("AI failure");
            }
            Game newChessGame = aiResult.getFirst();
            Move move = aiResult.getSecond();
            g1.update(newChessGame, move);
        }
        gameRepo.save(g1);
        versionMemo.put(g1);
        aliveMemo.put(g1.getId(), player.getColor());
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
    }

    private Optional<Pair<String, String>> decodeMoveString(String moveString) {
        Matcher matcher = MOVE_STRING.matcher(moveString);
        if (matcher.find()) {
            return Optional.of(Pair.of(matcher.group(1), matcher.group(2)));
        }
        return Optional.empty();
    }
}
