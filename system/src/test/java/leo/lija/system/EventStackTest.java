package leo.lija.system;

import leo.lija.chess.utils.Pair;
import leo.lija.system.entities.EventStack;
import leo.lija.system.entities.event.CastlingEvent;
import leo.lija.system.entities.event.CheckEvent;
import leo.lija.system.entities.event.EndEvent;
import leo.lija.system.entities.event.EnpassantEvent;
import leo.lija.system.entities.event.MessageEvent;
import leo.lija.system.entities.event.MoretimeEvent;
import leo.lija.system.entities.event.MoveEvent;
import leo.lija.system.entities.event.PossibleMovesEvent;
import leo.lija.system.entities.event.PromotionEvent;
import leo.lija.system.entities.event.RedirectEvent;
import leo.lija.system.entities.event.ReloadTableEvent;
import leo.lija.system.entities.event.StartEvent;
import leo.lija.system.entities.event.ThreefoldEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static leo.lija.chess.Color.BLACK;
import static leo.lija.chess.Color.WHITE;
import static leo.lija.chess.Pos.A1;
import static leo.lija.chess.Pos.A2;
import static leo.lija.chess.Pos.A3;
import static leo.lija.chess.Pos.A4;
import static leo.lija.chess.Pos.A7;
import static leo.lija.chess.Pos.A8;
import static leo.lija.chess.Pos.B7;
import static leo.lija.chess.Pos.B8;
import static leo.lija.chess.Pos.C1;
import static leo.lija.chess.Pos.C3;
import static leo.lija.chess.Pos.D1;
import static leo.lija.chess.Pos.D4;
import static leo.lija.chess.Pos.D6;
import static leo.lija.chess.Pos.D8;
import static leo.lija.chess.Pos.E1;
import static leo.lija.chess.Pos.E5;
import static leo.lija.chess.Pos.E8;
import static leo.lija.chess.Pos.F3;
import static leo.lija.chess.Pos.F5;
import static leo.lija.chess.Pos.F6;
import static leo.lija.chess.Pos.G3;
import static leo.lija.chess.Pos.G4;
import static leo.lija.chess.Role.QUEEN;
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
            new EnpassantEvent(F5),
            new MoveEvent(E1, C1, WHITE),
            new CastlingEvent(Pair.of(E1, C1), Pair.of(A1, D1), WHITE),
            new RedirectEvent("http://en.lichess.org/arstheien"),
            new MoveEvent(B7, B8, WHITE),
            new PromotionEvent(QUEEN, B8),
            new MoveEvent(A8, D8, WHITE),
            new CheckEvent(D6),
            new MessageEvent("foo", "http://foto.mail.ru/mail/annabuut/_myphoto/631.html#1491"),
            new MessageEvent("thibault", "message with a | inside"),
            new MessageEvent("0x1", "я слишком красив, чтобы ты это видела=)"),
            new ThreefoldEvent(),
            new ReloadTableEvent(),
            new MoretimeEvent(WHITE, 15),
            new EndEvent()
        );
        assertThat(EventStack.decode(stack.encode())).isEqualTo(stack);
    }
}
