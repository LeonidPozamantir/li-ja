package leo.lija.chess;

import leo.lija.chess.format.VisualFormat;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class Base {

    VisualFormat visual = new VisualFormat();

    public void beSituation(Situation situation, String visualString) {
        assertThat(visual.obj2Str(situation.board))
            .isEqualTo(visual.obj2Str(visual.str2Obj(visualString)));
    }

    public void beGame(Game game, String visualString) {
        assertThat(visual.obj2Str(game.getBoard()))
            .isEqualTo(visual.obj2Str(visual.str2Obj(visualString)));
    }

    public Optional<List<Pos>> pieceMoves(Piece piece, Pos pos) {
        return Optional.of(Board.empty().placeAt(piece, pos)).flatMap(b -> b.actorAt(pos).map(Actor::destinations));
    }
}
