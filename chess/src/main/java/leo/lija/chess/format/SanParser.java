package leo.lija.chess.format;

import com.google.common.labs.parse.Parser;
import com.google.mu.util.CharPredicate;
import io.vavr.Tuple4;
import leo.lija.chess.Pos;
import leo.lija.chess.Role;
import leo.lija.chess.utils.Pair;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Optional;

@UtilityClass
public class SanParser {

    public List<San> apply(String pattern) {
        return moves().parse(pattern);
    }

    public Parser<List<San>>.OrEmpty moves() {
        return move().zeroOrMoreDelimitedBy(" ");
    }

    public Parser<San> move() {
        return Parser.anyOf(kCastle(), qCastle(), std());
    }

    public Parser<San> kCastle() {
        return Parser.string("O-O").map((a) -> new KingSideCastle());
    }

    public Parser<San> qCastle() {
        return Parser.string("O-O-O").map((a) -> new QueenSideCastle());
    }

    public Parser<Std> std() {
        return Parser.sequence(disambiguated(), suffixes(), Std::withSuffixes);
    }

    public Parser<Std> disambiguated() {
        Parser<Pair<Optional<Role>, Optional<Character>>>.OrEmpty rofi = Parser.sequence(role(), file, Pair::of);
        Parser<Pair<Optional<Character>, Boolean>>.OrEmpty raca = Parser.sequence(rank, x, Pair::of);
        Parser<Tuple4<Optional<Role>, Optional<Character>, Optional<Character>, Boolean>>.OrEmpty rofiraca = Parser.sequence(rofi, raca,
            (p1, p2) -> new Tuple4<>(p1.getFirst(), p1.getSecond(), p2.getFirst(), p2.getSecond()));
        return Parser.sequence(rofiraca, dest().orElse(Pos.A1),
            (t4, p) -> new Std(p, t4._4, t4._1, t4._2, t4._3)).notEmpty();
    }

    public Parser<Suffixes>.OrEmpty suffixes() {
        Parser<Pair<Optional<Role>, Boolean>>.OrEmpty tmp = Parser.sequence(promotion().optional(), check, Pair::of);
        return Parser.sequence(tmp, checkmate, (p, cm) -> new Suffixes(p.getSecond(), cm, p.getFirst()));
    }

    Parser<Boolean>.OrEmpty x = exists("x");

    Parser<Boolean>.OrEmpty check = exists("+");

    Parser<Boolean>.OrEmpty checkmate = exists("#");

    public Parser<Boolean>.OrEmpty exists(String c) {
        return Parser.string(c).map(a -> true).orElse(false);
    }

    public Parser<Role> promotion() {
        return Parser.sequence(Parser.string("="), promotable(), (s, r) -> r);
    }

    public Parser<Role> promotable() {
        return Role.allPromotableByPgn.entrySet().stream()
            .reduce(Parser.string("N").map(a -> Role.KNIGHT),
                (acc, next) -> acc.or(Parser.string(next.getKey().toString()).map(a -> next.getValue())),
                (a, b) -> a
            );
    }

//    Parser<Optional<Character>>.OrEmpty file = rangeParser(List.of('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'));
//
//    Parser<Optional<Character>>.OrEmpty rank = rangeParser(List.of('1', '2', '3', '4', '5', '6', '7', '8'));

    Parser<Optional<Character>>.OrEmpty file = Parser.single(CharPredicate.range('a', 'h'), "file").optional();

    Parser<Optional<Character>>.OrEmpty rank = Parser.single(CharPredicate.range('0', '9'), "rank").optional();

//    public Parser<Optional<Character>>.OrEmpty rangeParser(List<Character> range) {
//        return range.stream()
//            .reduce(Parser.string(range.getFirst().toString()).map(a -> range.getFirst()),
//                (acc, next) -> acc.or(Parser.string(next.toString()).map(a -> next)),
//                (a, b) -> a
//            ).optional();
//    }

    public Parser<Optional<Role>>.OrEmpty role() {
        return Role.allByPgn.entrySet().stream()
            .reduce(Parser.string("N").map(a -> Role.KNIGHT),
                (acc, next) -> acc.or(Parser.string(next.getKey().toString()).map(a -> next.getValue())),
                (a, b) -> a
            ).optional();
    }

    public Parser<Pos> dest() {
        return Pos.allKeys.entrySet().stream()
            .reduce(Parser.string("a1").map(a -> Pos.A1),
                (acc, next) -> acc.or(Parser.string(next.getKey()).map(a -> next.getValue())),
                (a, b) -> a
            );
    }
}
