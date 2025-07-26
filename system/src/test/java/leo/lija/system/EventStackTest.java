package leo.lija.system;

import leo.lija.system.entities.EventStack;
import leo.lija.system.entities.event.EnpassantEvent;
import leo.lija.system.entities.event.MoveEvent;
import leo.lija.system.entities.event.PossibleMovesEvent;
import leo.lija.system.entities.event.StartEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static leo.lija.chess.Color.BLACK;
import static leo.lija.chess.Color.WHITE;
import static leo.lija.chess.Pos.A2;
import static leo.lija.chess.Pos.A3;
import static leo.lija.chess.Pos.A4;
import static leo.lija.chess.Pos.A7;
import static leo.lija.chess.Pos.A8;
import static leo.lija.chess.Pos.B8;
import static leo.lija.chess.Pos.C3;
import static leo.lija.chess.Pos.D4;
import static leo.lija.chess.Pos.E5;
import static leo.lija.chess.Pos.E8;
import static leo.lija.chess.Pos.F3;
import static leo.lija.chess.Pos.F5;
import static leo.lija.chess.Pos.F6;
import static leo.lija.chess.Pos.G3;
import static leo.lija.chess.Pos.G4;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("an event stack should")
class EventStackTest {

    @Test
    @DisplayName("encode and decode")
    void encodeDecode() {
        EventStack stack = EventStack.apply(
            new StartEvent(),
            new MoveEvent(G4, C3, BLACK),
            new PossibleMovesEvent(Map.of(A7, List.of(A8, B8))),
            new PossibleMovesEvent(Map.of(A2, List.of(A3, A4), F3, List.of(F5, G3, D4, E8))),
            new MoveEvent(E5, F6, WHITE),
            new EnpassantEvent(F5)
//            new Event("move", Map.of("from", "e1", "to", "c1", "color", "white")),
//            new Event("castling", Map.of("king", List.of("e1", "c1"), "rook", List.of("a1", "d1"), "color", "white")),
//            new Event("redirect", Map.of("url", "http://en.lichess.org/arstheien")),
//            new Event("move", Map.of("from", "b7", "to", "b8", "color", "white")),
//            new Event("promotion", Map.of("pieceClass", "queen", "key", "b8")),
//            new Event("move", Map.of("from", "b7", "to", "b6", "color", "white")),
//            new Event("check", Map.of("key", "d6")),
//            new Event("message", Map.of("message", List.of("foo", "http://foto.mail.ru/mail/annabuut/_myphoto/631.html#1491"))),
//            new Event("message", Map.of("message", List.of("0x1", "я слишком красив, чтобы ты это видела=)")))
        );
        assertThat(EventStack.decode(stack.encode())).isEqualTo(stack);
    }
}
