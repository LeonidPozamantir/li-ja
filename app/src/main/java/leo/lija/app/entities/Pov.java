package leo.lija.app.entities;

import leo.lija.chess.Color;

import java.util.Optional;

public record Pov(DbGame game, Color color) {

    public DbPlayer player() {
        return game.player(color);
    }

    public DbPlayer opponent() {
        return game.player(color.getOpposite());
    }

    public boolean isPlayerFullId(Optional<String> fullId) {
        return fullId.map(fid -> game.isPlayerFullId(player(), fid)).orElse(false);
    }

    public PovRef ref() {
        return new PovRef(game.getId(), color);
    }

    public static Pov apply(DbGame game, DbPlayer player) {
        return new Pov(game, player.getColor());
    }

    public static Optional<Pov> apply(DbGame game, String playerId) {
        return game.player(playerId).map(p -> new Pov(game, p.getColor()));
    }
}
