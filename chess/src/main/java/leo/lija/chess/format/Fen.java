package leo.lija.chess.format;

import leo.lija.chess.Board;
import leo.lija.chess.Color;
import leo.lija.chess.Game;
import leo.lija.chess.Piece;
import leo.lija.chess.Pos;
import leo.lija.chess.Role;
import leo.lija.chess.Situation;
import leo.lija.chess.utils.Pair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static leo.lija.chess.Pos.A8;
import static leo.lija.chess.Role.PAWN;

public class Fen {

    public Optional<Situation> str2Obj(String source) {
        LinkedList<Character> boardChars = source.replace("/", "").replaceAll("\\s*([\\w/]+)\\s.+", "$1").chars()
            .mapToObj(c -> (char) c)
            .collect(Collectors.toCollection(() -> new LinkedList<>()));

        Optional<Color> colorOption = Color.apply(source.replaceAll("^[\\w/]+\\s(\\w).+$", "$1").charAt(0));

        return colorOption
            .flatMap(color -> board(boardChars, A8)
                .map(pieces -> new Situation(new Board(pieces.stream().collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))), color)));
    }

    private Optional<List<Pair<Pos, Piece>>> board(LinkedList<Character> chars, Pos pos) {
        if (chars.isEmpty()) return Optional.of(List.of());
        else {
            char c = chars.getFirst();
            chars.removeFirst();
            if (c < 58) {
                return tore(pos, c - 48).flatMap(p -> board(chars, p));
            }
            return Role.byFen(Character.toLowerCase(c))
                .map(role -> {
                    Pair<Pos, Piece> firstPair = Pair.of(pos, new Piece(Color.apply(Character.isUpperCase(c)), role));
                    List<Pair<Pos, Piece>> otherPairs = tore(pos, 1).flatMap(p -> board(chars, p)).orElse(new LinkedList<>());
                    otherPairs.addFirst(firstPair);
                    return otherPairs;
                });
        }
    }

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

    public Optional<Pos> tore(Pos pos, int n) {
        return Pos.posAt(
            (pos.getX() + n - 1) % 8 + 1,
            pos.getY() - (pos.getX() + n - 1) / 8
        );
    }

    private String exportBoard(Board board) {
        List<String> outs = new ArrayList<>();
        for (int j = 8; j >= 1; j--) {
            StringBuilder out = new StringBuilder("");
            int empty = 0;
            for (int i = 1; i <= 8; i++) {
                Optional<Piece> optPiece = board.at(i, j);
                if (optPiece.isEmpty()) empty++;
                else if (empty == 0) out.append(optPiece.get().fen());
                else {
                    out.append(empty).append(optPiece.get().fen());
                    empty = 0;
                }
            }
            if (empty == 0) outs.add(out.toString()); else outs.add(out.toString() + empty);
        }
        return outs.stream().collect(Collectors.joining("/"));
    }
}
