package leo.lija.system.entities;


import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import leo.lija.chess.Board;
import leo.lija.chess.Color;
import leo.lija.chess.Game;
import leo.lija.chess.History;
import leo.lija.chess.Piece;
import leo.lija.chess.Pos;
import leo.lija.chess.utils.Pair;
import leo.lija.system.Piotr;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static leo.lija.chess.Color.BLACK;
import static leo.lija.chess.Color.WHITE;
import static leo.lija.chess.Pos.A1;

@Entity
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@AllArgsConstructor
@Getter
public class DbGame {

    @Id
    private String id;

    @ElementCollection
    private List<Player> players;

    @NotNull
    @Column(nullable = false)
    private String pgn;
    private int status;
    private int turns;
    private int variant;

    public Game toChess() {
        Map<Pos, Piece> pieces = players.stream()
            .flatMap(player -> {
                Color color = Color.allByName.get(player.getColor());
               return Arrays.stream(player.getPs().split(" "))
                   .map(piece -> {
                       char[] codes = piece.toCharArray();
                       if (codes.length < 2) {
                           Optional<Pair<Pos, Piece>> res = Optional.empty();
                           return res;
                       }
                       char pos = codes[0];
                       char role = codes[1];
                       return posPiece(pos, role, color);
                   })
                   .filter(Optional::isPresent)
                   .map(Optional::get);
            })
            .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
        return new Game(
            new Board(pieces, new History()),
            0 == turns % 2 ? WHITE : BLACK,
            pgn
            );
    }

    private Optional<Pair<Pos, Piece>> posPiece(char posCode, char roleCode, Color color) {
        return Optional.ofNullable(Piotr.decodePos.get(posCode))
            .flatMap(pos -> Optional.ofNullable(Piotr.decodeRole.get(roleCode))
                .map(role -> Pair.of(pos, new Piece(color, role))));
    }

}
