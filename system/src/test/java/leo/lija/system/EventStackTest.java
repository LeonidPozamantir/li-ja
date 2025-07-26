package leo.lija.system;

import leo.lija.system.entities.Event;
import leo.lija.system.entities.EventStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("an event stack should")
class EventStackTest {

    @Test
    @DisplayName("encode and decode")
    void encodeDecode() {
        EventStack stack = new EventStack(List.of(
            new Event("start"),
            new Event("move", Map.of("from", "g4", "to", "c3", "color", "black")),
            new Event("possible_moves", Map.of("possible_moves", Map.of("a7", "a8b8"))),
            new Event("possible_moves", Map.of("possible_moves", Map.of("a2", "a3a4", "f3", "f5g3d4e8"))),
            new Event("move", Map.of("from", "e5", "to", "f6", "color", "white")),
            new Event("enpassant", Map.of("killed", "f5")),
            new Event("move", Map.of("from", "e1", "to", "c1", "color", "white")),
            new Event("castling", Map.of("king", List.of("e1", "c1"), "rook", List.of("a1", "d1"), "color", "white")),
            new Event("redirect", Map.of("url", "http://en.lichess.org/arstheien")),
            new Event("move", Map.of("from", "b7", "to", "b8", "color", "white")),
            new Event("promotion", Map.of("pieceClass", "queen", "key", "b8")),
            new Event("move", Map.of("from", "b7", "to", "b6", "color", "white")),
            new Event("check", Map.of("key", "d6")),
            new Event("message", Map.of("message", List.of("foo", "http://foto.mail.ru/mail/annabuut/_myphoto/631.html#1491"))),
            new Event("message", Map.of("message", List.of("0x1", "я слишком красив, чтобы ты это видела=)")))
        ));
        assertThat(EventStack.decode(stack.encode())).isEqualTo(stack);
    }
}
