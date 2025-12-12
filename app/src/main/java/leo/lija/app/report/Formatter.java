package leo.lija.app.report;

import leo.lija.chess.utils.Pair;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

public class Formatter {

    @RequiredArgsConstructor
    public static class DataLine {

        private final List<Pair<String, Object>> data;

        public String header() {
            return data.stream().map(Pair::getFirst).collect(Collectors.joining(" "));
        }

        public String line() {
            return data.stream().map(p -> {
                String name = p.getFirst();
                Object value = p.getSecond();
                String s = value.toString();
                return " ".repeat(Math.max(name.length() - s.length(), 0)) + s + " ";
            }).collect(Collectors.joining());
        }

    }

    public static DataLine dataLine(List<Pair<String, Object>> data) {
        return new DataLine(data);
    }
}
