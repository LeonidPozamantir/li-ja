package leo.lija.chess.format;

import leo.lija.chess.Actor;
import leo.lija.chess.Move;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

import static leo.lija.chess.Role.PAWN;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PgnDump {

    public static String move(Move m) {
        String capturing = m.captures() ? "x" : "";
        String checking = m.checkmates() ? "#" : m.checks() ? "+" : "";

        List<Actor> candidates = m.situation().actors().stream()
            .filter(a -> !a.getPos().equals(m.orig()) && a.getPiece().role().equals(m.piece().role()) && a.destinations().contains(m.dest()))
            .toList();

        String disambiguate = candidates.isEmpty()
            ? ""
            : candidates.stream().anyMatch(a -> a.getPos().isVertical(m.orig()))
                ? m.orig().file() + m.orig().rank() : m.orig().file();

        String pgn;
        if (m.castle()) pgn = m.orig().toRight(m.dest()) ? "O-O-O" : "O-O";
        else if (m.enpassant()) pgn = m.orig().file() + "x" + m.dest().rank();
        else if (m.promotion().isPresent()) pgn = m.dest().key() + m.promotion().get().pgn();
        else if (m.piece().role().equals(PAWN) && m.captures()) pgn = m.orig().file() + "x" + m.dest().key();
        else if (m.piece().role().equals(PAWN)) pgn = m.dest().key();
        else pgn = m.piece().role().pgn() + disambiguate + capturing + m.dest().key();
        return pgn + checking;
    }

}
