package leo.lija.chess.utils;

import lombok.experimental.UtilityClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class CsvLoader {

    public static List<List<String>> load(String file) throws IOException {
        List<List<String>> res = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(
                CsvLoader.class.getResourceAsStream("/" + file)))) {

            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                res.add(List.of(parts));
            }
        }

        return res;
    }
}
