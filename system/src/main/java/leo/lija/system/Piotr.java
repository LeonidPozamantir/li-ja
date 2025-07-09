package leo.lija.system;

import leo.lija.chess.Pos;
import leo.lija.chess.Role;

import static leo.lija.chess.Pos.*;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Piotr {

    public static Map<Character, Pos> decodePos = Map.ofEntries(
        Map.entry('a', A1), Map.entry('b', B1), Map.entry('c', C1), Map.entry('d', D1),
        Map.entry('e', E1), Map.entry('f', F1), Map.entry('g', G1), Map.entry('h', H1),
        Map.entry('i', A2), Map.entry('j', B2), Map.entry('k', C2), Map.entry('l', D2),
        Map.entry('m', E2), Map.entry('n', F2), Map.entry('o', G2), Map.entry('p', H2),
        Map.entry('q', A3), Map.entry('r', B3), Map.entry('s', C3), Map.entry('t', D3),
        Map.entry('u', E3), Map.entry('v', F3), Map.entry('w', G3), Map.entry('x', H3),
        Map.entry('y', A4), Map.entry('z', B4), Map.entry('A', C4), Map.entry('B', D4),
        Map.entry('C', E4), Map.entry('D', F4), Map.entry('E', G4), Map.entry('F', H4),
        Map.entry('G', A5), Map.entry('H', B5), Map.entry('I', C5), Map.entry('J', D5),
        Map.entry('K', E5), Map.entry('L', F5), Map.entry('M', G5), Map.entry('N', H5),
        Map.entry('O', A6), Map.entry('P', B6), Map.entry('Q', C6), Map.entry('R', D6),
        Map.entry('S', E6), Map.entry('T', F6), Map.entry('U', G6), Map.entry('V', H6),
        Map.entry('W', A7), Map.entry('X', B7), Map.entry('Y', C7), Map.entry('Z', D7),
        Map.entry('0', E7), Map.entry('1', F7), Map.entry('2', G7), Map.entry('3', H7),
        Map.entry('4', A8), Map.entry('5', B8), Map.entry('6', C8), Map.entry('7', D8),
        Map.entry('8', E8), Map.entry('9', F8), Map.entry('!', G8), Map.entry('?', H8)
    );

    public static Map<Pos, Character> encodePos = decodePos.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

    public static Map<Character, Role> decodeRole = Role.all.stream()
        .collect(Collectors.toMap(r -> r.fen, Function.identity()));
}
