package leo.lija.system;

import leo.lija.chess.Clock;
import leo.lija.chess.Color;
import leo.lija.chess.Game;
import leo.lija.chess.Move;
import leo.lija.chess.Pos;
import leo.lija.chess.utils.Pair;
import leo.lija.system.entities.DbGame;
import leo.lija.system.entities.DbPlayer;
import leo.lija.system.entities.Status;

import java.util.List;
import java.util.Optional;

public class RichDbGame extends DbGame {
    public RichDbGame(String id, DbPlayer whitePlayer, DbPlayer blackPlayer, String pgn, Status status, int turns, Optional<Clock> clock, Optional<String> lastMove, Color creatorColor) {
        super(id, whitePlayer, blackPlayer, pgn, status, turns, clock, lastMove, creatorColor);
    }

    public RichDbGame(DbGame game) {
        this(game.getId(), game.getWhitePlayer(), game.getBlackPlayer(), game.getPgn(), game.getStatus(), game.getTurns(), game.getClock(), game.getLastMove(), game.getCreatorColor());
    }

    @Override
    public RichDbGame copy() {
        return new RichDbGame(super.copy());
    }

    public RichDbGame withoutEvents() {
        mapPlayers(p -> {
            DbPlayer cp = p.copy();
            cp.setEvts("");
            return cp;
        });
        return this;
    }

    public RichDbGame afterMove(Pos orig, Pos dest) {
        Pair<Game, Move> pair = this.toChess().apply(orig, dest);
        this.update(pair.getFirst(), pair.getSecond());
        return this;
    }
}
