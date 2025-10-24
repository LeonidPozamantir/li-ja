package leo.lija.app;

import leo.lija.chess.Move;
import leo.lija.chess.utils.Pair;
import leo.lija.app.entities.EventStack;
import leo.lija.app.entities.event.CastlingEvent;
import leo.lija.app.entities.event.CheckEvent;
import leo.lija.app.entities.event.EndEvent;
import leo.lija.app.entities.event.EnpassantEvent;
import leo.lija.app.entities.event.Event;
import leo.lija.app.entities.event.MessageEvent;
import leo.lija.app.entities.event.MoretimeEvent;
import leo.lija.app.entities.event.MoveEvent;
import leo.lija.app.entities.event.PossibleMovesEvent;
import leo.lija.app.entities.event.PromotionEvent;
import leo.lija.app.entities.event.RedirectEvent;
import leo.lija.app.entities.event.ReloadTableEvent;
import leo.lija.app.entities.event.StartEvent;
import leo.lija.app.entities.event.ThreefoldEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static leo.lija.chess.Color.BLACK;
import static leo.lija.chess.Color.WHITE;
import static leo.lija.chess.Pos.A1;
import static leo.lija.chess.Pos.A2;
import static leo.lija.chess.Pos.A3;
import static leo.lija.chess.Pos.A4;
import static leo.lija.chess.Pos.A5;
import static leo.lija.chess.Pos.A7;
import static leo.lija.chess.Pos.A8;
import static leo.lija.chess.Pos.B7;
import static leo.lija.chess.Pos.B8;
import static leo.lija.chess.Pos.C1;
import static leo.lija.chess.Pos.C3;
import static leo.lija.chess.Pos.D1;
import static leo.lija.chess.Pos.D2;
import static leo.lija.chess.Pos.D4;
import static leo.lija.chess.Pos.D5;
import static leo.lija.chess.Pos.D6;
import static leo.lija.chess.Pos.D7;
import static leo.lija.chess.Pos.D8;
import static leo.lija.chess.Pos.E1;
import static leo.lija.chess.Pos.E3;
import static leo.lija.chess.Pos.E5;
import static leo.lija.chess.Pos.E6;
import static leo.lija.chess.Pos.E8;
import static leo.lija.chess.Pos.F1;
import static leo.lija.chess.Pos.F3;
import static leo.lija.chess.Pos.F5;
import static leo.lija.chess.Pos.F6;
import static leo.lija.chess.Pos.G1;
import static leo.lija.chess.Pos.G3;
import static leo.lija.chess.Pos.G4;
import static leo.lija.chess.Pos.H1;
import static leo.lija.chess.Role.QUEEN;
import static leo.lija.chess.Role.ROOK;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("an event stack should")
class EventStackTest extends Fixtures {

    @Test
    @DisplayName("encode and decode all events without loss")
    void encodeDecode() {
        EventStack stack = EventStack.build(
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

    @Test
    @DisplayName("decode and re-encode production data events")
    void decodeEncodeProduction() {
        assertThat(dbGame5.players()).isNotEmpty().allMatch(player ->
            (EventStack.decode(player.getEvts())).encode().equals(player.getEvts())
        );
    }

    @Nested
    @DisplayName("optimize events")
    class OptimizeEvents {

        @Test
        @DisplayName("empty duplicated possible move events")
        void emptyDuplicated() {
            assertThat(EventStack.build(
                new StartEvent(),
                new MoveEvent(G4, C3, BLACK),
                new PossibleMovesEvent(Map.of(A7, List.of(A8, B8))),
                new MoveEvent(E5, F6, WHITE),
                new PossibleMovesEvent(Map.of(A2, List.of(A3, A4), F3, List.of(F5, G3, D4, E8))),
                new MoveEvent(G4, C3, BLACK),
                new PossibleMovesEvent(Map.of(A5, List.of(A8, B8))),
                new MoretimeEvent(WHITE, 15),
                new EndEvent()
            ).optimize()).isEqualTo(EventStack.build(
                new StartEvent(),
                new MoveEvent( G4,  C3,  BLACK),
                new PossibleMovesEvent(Map.of()),
                new MoveEvent( E5,  F6,  WHITE),
                new PossibleMovesEvent(Map.of()),
                new MoveEvent( G4,  C3,  BLACK),
                new PossibleMovesEvent(Map.of(A5, List.of(A8, B8))),
                new MoretimeEvent(WHITE, 15),
                new EndEvent()
            ));
        }

        @Test
        @DisplayName("keep only the " + EventStack.MAX_EVENTS + " more recent events")
        void keepRecent() {
            int nb = EventStack.MAX_EVENTS;
            Event someEvent = new CheckEvent(D6);
            Event endEvent = new EndEvent();
            Event[] events = Stream.concat(
                Stream.generate(() -> someEvent).limit(nb + 40),
                Stream.of(endEvent)
            ).toArray(Event[]::new);
            EventStack stack = EventStack.build(events);
            List<Event> expected = Stream.concat(
                Stream.generate(() -> someEvent).limit(nb - 1),
                Stream.of(endEvent)
            ).toList();
            assertThat(stack.optimize().getEvents().stream().map(Pair::getSecond).toList()).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("apply move events")
    class ApplyMoveEvents {

        private EventStack addMoves(EventStack eventStack, Move...moves) {
            return Arrays.stream(moves).reduce(eventStack, (stack, move) -> stack.withEvents(Event.fromMove(move)), (s1, s2) -> s1);
        }

        @Test
        @DisplayName("start with no events")
        void startWithNoEvents() {
            assertThat(EventStack.apply().getEvents()).isEmpty();
        }

        @Test
        void move() {
            EventStack stack = addMoves(EventStack.apply(), newMove(WHITE.pawn(), D2, D4));
            assertThat(stack.getEvents()).isEqualTo(List.of(Pair.of(1, new MoveEvent(D2, D4, WHITE))));
        }

        @Test
        void capture() {
            EventStack stack = addMoves(EventStack.apply(), newMove(WHITE.pawn(), D2, E3, Optional.of(E3)));
            assertThat(stack.getEvents()).isEqualTo(List.of(Pair.of(1, new MoveEvent(D2, E3, WHITE))));
        }

        @Test
        void enpassant() {
            EventStack stack = addMoves(EventStack.apply(), newMove(WHITE.pawn(), D5, E6, Optional.of(E5), true));
            assertThat(stack.getEvents()).isEqualTo(List.of(
                Pair.of(1, new MoveEvent(D5, E6, WHITE)),
                Pair.of(2, new EnpassantEvent(E5))
            ));
        }

        @Test
        void promotion() {
            EventStack stack = addMoves(EventStack.apply(), newMoveWithPromotion(WHITE.pawn(), D7, D8, Optional.of(ROOK)));
            assertThat(stack.getEvents()).isEqualTo(List.of(
                Pair.of(1, new MoveEvent(D7, D8, WHITE)),
                Pair.of(2, new PromotionEvent(ROOK, D8))
            ));
        }

        @Test
        void castling() {
            EventStack stack = addMoves(EventStack.apply(), newMoveWithCastle(WHITE.king(), E1, G1, Optional.of(Pair.of(Pair.of(E1, G1), Pair.of(H1, F1)))));
            assertThat(stack.getEvents()).isEqualTo(List.of(
                Pair.of(1, new MoveEvent(E1, G1, WHITE)),
                Pair.of(2, new CastlingEvent(Pair.of(E1, G1), Pair.of(H1, F1), WHITE))
            ));
        }

        @Test
        @DisplayName("two moves")
        void twoMoves() {
            EventStack stack = addMoves(EventStack.apply(),
                newMove(WHITE.pawn(), D2, D4),
                newMove(BLACK.pawn(), D7, D5));
            assertThat(stack.getEvents()).isEqualTo(List.of(
                Pair.of(1, new MoveEvent(D2, D4, WHITE)),
                Pair.of(2, new MoveEvent(D7, D5, BLACK))
            ));
        }
    }

    @Nested
    @DisplayName("get versions")
    class GetVersions {

        EventStack stack = new EventStack(List.of(
            Pair.of(21, new ThreefoldEvent()),
            Pair.of(23, new EndEvent()),
            Pair.of(22, new ThreefoldEvent()),
            Pair.of(19, new StartEvent())
        ));

        @Test
        @DisplayName("first version")
        void firstVersion() {
            assertThat(stack.firstVersion()).isEqualTo(19);
        }

        @Test
        @DisplayName("last version")
        void lastVersion() {
            assertThat(stack.lastVersion()).isEqualTo(23);
        }
    }

    @Nested
    @DisplayName("get events")
    class GetEvents {

        @Nested
        @DisplayName("since version")
        class SinceVersion {

            @Test
            @DisplayName("empty stack")
            void emptyStack() {
                assertThat(EventStack.apply().eventsSince(12)).isEmpty();
            }

            List<Pair<Integer, Event>> threeEvents = List.of(
                Pair.of(20, new StartEvent()),
                Pair.of(21, new ThreefoldEvent()),
                Pair.of(22, new EndEvent())
            );
            List<Event> threeEventsValues = threeEvents.stream().map(Pair::getSecond).toList();
            List<Pair<Integer, Event>> unordered = List.of(
                Pair.of(21, new ThreefoldEvent()),
                Pair.of(22, new EndEvent()),
                Pair.of(20, new StartEvent())
            );

            @Test
            @DisplayName("too old version")
            void tooOldVersion() {
                assertThat(new EventStack(threeEvents).eventsSince(12)).isEmpty();
            }

            @Test
            @DisplayName("too new version")
            void tooNewVersion() {
                assertThat(new EventStack(threeEvents).eventsSince(23)).isEmpty();
            }

            @Test
            @DisplayName("latest version")
            void latestVersion() {
                assertThat(new EventStack(threeEvents).eventsSince(22)).contains(List.of());
            }

            @Test
            @DisplayName("first version")
            void firstVersion() {
                assertThat(new EventStack(threeEvents).eventsSince(19)).contains(threeEventsValues);
            }

            @Test
            @DisplayName("first version, unordered events")
            void firstVersionUnordered() {
                assertThat(new EventStack(unordered).eventsSince(19)).contains(threeEventsValues);
            }

            @Test
            @DisplayName("latest version, unordered events")
            void latestVersionUnordered() {
                assertThat(new EventStack(unordered).eventsSince(19)).contains(threeEventsValues);
            }

            @Test
            @DisplayName("middle version, unordered events")
            void middleVersionUnordered() {
                assertThat(new EventStack(unordered).eventsSince(21)).contains(List.of(
                    new EndEvent()
                ));
            }
        }
    }
}
