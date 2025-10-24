package leo.lija.app;

import leo.lija.chess.Clock;
import leo.lija.chess.Color;
import leo.lija.chess.Game;
import leo.lija.chess.Move;
import leo.lija.chess.Pos;
import leo.lija.chess.utils.Pair;
import leo.lija.app.entities.DbGame;
import leo.lija.app.entities.DbPlayer;
import leo.lija.app.entities.Status;

import java.util.Optional;

public class RichDbGame extends DbGame {
    public RichDbGame(String id, DbPlayer whitePlayer, DbPlayer blackPlayer, String pgn, Status status, int turns, Optional<Clock> clock, Optional<String> lastMove, Optional<Pos> check, Color creatorColor) {
        super(id, whitePlayer, blackPlayer, pgn, status, turns, clock, lastMove, check, creatorColor);
    }

    public RichDbGame(DbGame game) {
        this(game.getId(), game.getWhitePlayer(), game.getBlackPlayer(), game.getPgn(), game.getStatus(), game.getTurns(), game.getClock(), game.getLastMove(), game.getCheck(), game.getCreatorColor());
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
