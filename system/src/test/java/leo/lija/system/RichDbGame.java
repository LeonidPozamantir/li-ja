package leo.lija.system;

import leo.lija.chess.Game;
import leo.lija.chess.Move;
import leo.lija.chess.Pos;
import leo.lija.chess.utils.Pair;
import leo.lija.system.entities.DbClock;
import leo.lija.system.entities.DbGame;
import leo.lija.system.entities.DbPlayer;

import java.util.List;

public class RichDbGame extends DbGame {
    public RichDbGame(String id, List<DbPlayer> players, String pgn, int status, int turns, DbClock clock, String lastMove, String positionHashes, String castles) {
        super(id, players, pgn, status, turns, clock, lastMove, positionHashes, castles);
    }

    public RichDbGame(DbGame game) {
        this(game.getId(), game.getPlayers(), game.getPgn(), game.getStatus(), game.getTurns(), game.getClock(), game.getLastMove(), game.getPositionHashes(), game.getCastles());
    }

    @Override
    public RichDbGame copy() {
        return new RichDbGame(super.copy());
    }

    public RichDbGame withoutEvents() {
        getPlayers().stream().forEach(p -> p.setEvts(""));
        return this;
    }

    public RichDbGame afterMove(Pos orig, Pos dest) {
        Pair<Game, Move> pair = this.toChess().apply(orig, dest, null);
        this.update(pair.getFirst(), pair.getSecond());
        return this;
    }
}
