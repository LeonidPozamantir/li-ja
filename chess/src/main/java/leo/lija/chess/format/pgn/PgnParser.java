package leo.lija.chess.format.pgn;

import com.google.common.labs.parse.CharacterSet;
import com.google.common.labs.parse.Parser;
import io.vavr.Tuple4;
import leo.lija.chess.Pos;
import leo.lija.chess.Role;
import leo.lija.chess.Side;
import leo.lija.chess.utils.Pair;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@UtilityClass
public class PgnParser {

    public ParsedPgn apply(String pgn) {
        return all().parse(pgn);
    }

    public Parser<ParsedPgn> all() {
        return Parser.anyOf(
            Parser.sequence(tags(), moves().followedBy(result().optional()), ParsedPgn::new),
            moves().map(m -> new ParsedPgn(List.of(), m))
        );
    }

    public Parser<List<Tag>> tags() {
        return tag().followedBy(Parser.zeroOrMore(Character::isWhitespace, "ki")).atLeastOnce();
    }

    private Parser<String>.OrEmpty whitespace() {
        return Parser.zeroOrMore(Character::isWhitespace, "ki");
    }

    public Parser<Tag> tag() {
        return Parser.sequence(tagName(), tagValue().followedBy(crlf()), (name, value) -> {
           if (name.equals("FEN")) return new Fen(value);
           return new Unknown(name, value);
        });
    }

    public Parser<String> tagName() {
        return Parser.string("[").then(Parser.word());
    }

    public Parser<String> tagValue() {
        return Parser.quotedBy(" \"", "\"]");
    }

    public Parser<List<San>> moves() {
        return move().atLeastOnce();
    }

    public Parser<String> result() {
        return Parser.anyOf(Parser.string("*"), Parser.string("1/2-1/2"), Parser.string("0-1"), Parser.string("1-0"));
    }

    public Parser<San> move() {
        return Parser.anyOf(
                number().then(Parser.anyOf(qCastle(), kCastle(), std())),
                Parser.anyOf(qCastle(), kCastle(), std())
            ).followedBy(comment().optional())
            .followedBy(whitespace());
    }

    public Parser<String> comment() {
        return Parser.quotedBy(" {", "}");
    }

    public Parser<San> qCastle() {
        return Parser.string("O-O-O").map((a) -> new Castle(Side.QUEEN_SIDE));
    }

    public Parser<San> kCastle() {
        return Parser.string("O-O").map((a) -> new Castle(Side.KING_SIDE));
    }

    public Parser<Std> std() {
        return Parser.sequence(
            Parser.anyOf(simple(), disambiguated()),
            suffixes(),
            Std::withSuffixes);
    }

    public Parser<String> number() {
        return Parser.digits().then(Parser.consecutive(CharacterSet.charsIn("[. ]")));
    }

    public Parser<Std> simple() {
        Parser<Pair<Role, Boolean>>.OrEmpty roca = Parser.sequence(role(), x, Pair::of);
        return Parser.anyOf(
            Parser.sequence(roca.notEmpty(), dest(), (t2, p) -> new Std(p, t2.getFirst(), t2.getSecond())),
            dest().map(p -> new Std(p, Role.PAWN, false))
        );
    }

    public Parser<Std> disambiguated() {
        Parser<Pair<Role, Optional<Integer>>>.OrEmpty rofi = Parser.sequence(role(), file.optional(), Pair::of);
        Parser<Pair<Optional<Integer>, Boolean>>.OrEmpty raca = Parser.sequence(rank.optional(), x, Pair::of);
        Parser<Tuple4<Role, Optional<Integer>, Optional<Integer>, Boolean>>.OrEmpty rofiraca = Parser.sequence(rofi, raca,
            (p1, p2) -> new Tuple4<>(p1.getFirst(), p1.getSecond(), p2.getFirst(), p2.getSecond()));
        return Parser.anyOf(
            Parser.sequence(rofiraca.notEmpty(), dest(), (t4, p) -> new Std(p, t4._1, t4._4, t4._2, t4._3)),
            dest().map(p -> new Std(p, Role.PAWN, false))
        );
    }


    public Parser<Suffixes>.OrEmpty suffixes() {
        Parser<Pair<Optional<Role>, Boolean>>.OrEmpty tmp = Parser.sequence(promotion().optional(), check, Pair::of);
        return Parser.sequence(tmp, checkmate, (p, cm) -> new Suffixes(p.getSecond(), cm, p.getFirst()));
    }

    public Parser<String> crlf() {
        return Parser.string("\r\n").or(Parser.string("\n"));
    }

    Parser<Boolean>.OrEmpty x = exists("x");

    Parser<Boolean>.OrEmpty check = exists("+");

    Parser<Boolean>.OrEmpty checkmate = exists("#");

    public Parser<Role>.OrEmpty role() {
        return mapParser(Role.allByPgn).orElse(Role.PAWN);
    }

    Parser<Integer> file = mapParser(rangeToMap(List.of('a','b','c','d','e','f','g','h')));

    Parser<Integer> rank = mapParser(rangeToMap(List.of('1','2','3','4','5','6','7','8')));

    public Parser<Role> promotion() {
        return Parser.string("=").then(mapParser(Role.allPromotableByPgn));
    }

    public Parser<Pos> dest() {
        return mapParser(Pos.allKeys);
    }

    public Parser<Boolean>.OrEmpty exists(String c) {
        return Parser.string(c).map(a -> true).orElse(false);
    }

    public Map<Character, Integer> rangeToMap(Iterable<Character> r) {
        Map<Character, Integer> map = new HashMap<>();
        int index = 1;
        for (Character c : r) {
            map.put(c, index++);
        }
        return map;
    }

    public <A, B> Parser<B> mapParser(Map<A, B> map) {
        Map.Entry<A, B> firstEntry = map.entrySet().stream().findFirst().orElse(null);
        assert firstEntry != null;
        return map.entrySet().stream()
            .reduce(Parser.string(firstEntry.getKey().toString()).map(a -> firstEntry.getValue()),
                (acc, next) -> acc.or(Parser.string(next.getKey().toString()).map(a -> next.getValue())),
                (a, b) -> a
            );
    }
}
