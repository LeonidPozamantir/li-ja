package leo.lija.chess.format;

import leo.lija.chess.Board;
import leo.lija.chess.Game;
import leo.lija.chess.Piece;
import leo.lija.chess.Pos;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static leo.lija.chess.Role.PAWN;

public class Fen implements Format<Game> {

    @Override
    public Game str2Obj(String source) {
        return new Game();
    }

    @Override
    public String obj2Str(Game game) {
        String lastMoveFen = game.getBoard().getHistory().lastMove()
            .flatMap(lastMove -> {
                Pos orig = lastMove.getFirst();
                Pos dest = lastMove.getSecond();
                return game.getBoard().at(dest)
                    .filter(piece -> piece.is(PAWN))
                    .flatMap(piece -> {
                        if (orig.getY() == 2 && dest.getY() == 4) return dest.down();
                        else if (orig.getY() == 7 && dest.getY() == 5) return dest.up();
                        else return Optional.empty();
                    })
                    .map(Pos::toString);
            })
            .orElse("-");
        return List.of(
            exportBoard(game.getBoard()),
            Character.toString(game.getPlayer().getLetter()),
            game.getBoard().getHistory().castleNotation(),
            lastMoveFen,
            Integer.toString(game.halfMoveClock()),
            Integer.toString(game.fullMoveNumber())
        ).stream().collect(Collectors.joining(" "));
    }

    private String exportBoard(Board board) {
        List<String> outs = new ArrayList<>();
        for (int j = 8; j >= 1; j--) {
            String out = "";
            int empty = 0;
            for (int i = 1; i <= 8; i++) {
                Optional<Piece> optPiece = board.at(i, j);
                if (optPiece.isEmpty()) empty++;
                else if (empty == 0) out += optPiece.get().fen();
                else {
                    out += Integer.toString(empty) + optPiece.get().fen();
                    empty = 0;
                }
            }
            if (empty == 0) outs.add(out); else outs.add(out + empty);
        }
        return outs.stream().collect(Collectors.joining("/"));
    }
}
