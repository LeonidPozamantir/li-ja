package leo.lija.system;

import leo.lija.chess.Game;
import leo.lija.chess.Move;
import leo.lija.chess.utils.Pair;
import leo.lija.system.entities.DbGame;
import leo.lija.system.exceptions.AppException;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class CraftyAi implements Ai {

    private final String execPath;
    private final Optional<String> bookPath;

    public CraftyAi() {
        this("crafty", Optional.empty());
    }

    @Override
    public Pair<Game, Move> apply(DbGame dbGame) {
        throw new AppException("Not implemented");
    }
}
