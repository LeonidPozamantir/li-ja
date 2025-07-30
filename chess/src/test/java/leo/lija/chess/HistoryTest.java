package leo.lija.chess;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("threefold repetition")
class HistoryTest {

    private String toHash(Object a) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] hashBytes = md.digest(a.toString().getBytes());
        return HexFormat.of().formatHex(hashBytes);
    }

    private History makeHistory(List<Object> positions) {
        return positions.stream().map(this::toHash).reduce(new History(), (history, hash) -> history.withNewPositionHash(hash), (h1, h2) -> h1);
    }

    @Test
    @DisplayName("empty history")
    void emptyHistory() {
        assertThat(new History().threefoldRepetition()).isFalse();
    }

    @Test
    @DisplayName("not 3 same elements")
    void not3SameElements() {
        History history = makeHistory(List.of(1, 2, 3, 4, 5, 2, 5, 6, 23, 55));
        assertThat(history.threefoldRepetition()).isFalse();
    }

    @Test
    @DisplayName("not 3 elements same as the last one")
    void not3SameAsLast() {
        History history = makeHistory(List.of(1, 2, 3, 4, 5, 2, 5, 6, 23, 2, 55));
        assertThat(history.threefoldRepetition()).isFalse();
    }

    @Test
    void positive() {
        History history = makeHistory(List.of(1, 2, 3, 4, 5, 2, 5, 6, 23, 2));
        assertThat(history.threefoldRepetition()).isTrue();
    }
}
