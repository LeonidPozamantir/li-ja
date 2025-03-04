package leo.lija.format;

import leo.lija.model.Board;
import leo.lija.model.Color;
import leo.lija.model.Piece;
import leo.lija.model.Pos;
import leo.lija.model.Role;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * r bqkb r
 * p ppp pp
 * pr
 *    P p
 *    QnB
 *  PP  N
 * P    PPP
 * RN  K  R
 */

@Service
public class Visual implements Format<Board> {

    private final Map<Character, Role> pieces = Role.all.stream().collect(HashMap::new, (m, r) -> m.put(r.fen, r), Map::putAll);

    @Override
    public Board str2Obj(String str) {
        List<String> rawLines = List.of(str.split("\n"));
        List<String> lines = rawLines.size() == 8 ? rawLines : rawLines.subList(0, 8);

        Map<Pos, Piece> boardPieces = new HashMap<>();
        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                if (c != ' ') {
                    Role role = pieces.get(Character.toLowerCase(c));
                    if (role != null) {
                        boardPieces.put(new Pos(x + 1, 8 - y), new Piece(Color.isW(Character.isUpperCase(c)), role));
                    }
                }
            }
        }
        return new Board(boardPieces);
    }

    @Override
    public String Obj2Str(Board board) {
        String s = IntStream.range(0, 8)
                .mapToObj(y -> IntStream.range(0, 8)
                        .mapToObj(x -> board.at(new Pos(x + 1, 8 - y))
                                .map(Piece::fen)
                                .map(String::valueOf)
                                .orElse(" "))
                        .collect(Collectors.joining())
                        .replaceFirst("\\s*$", ""))
                .collect(Collectors.joining("\n"));
        return s;
    }
}
