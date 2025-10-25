package leo.lija.system.memo;

import jakarta.annotation.PostConstruct;
import leo.lija.system.exceptions.AppException;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class EntryMemo {

    private final Supplier<Optional<Integer>> getId;

    private int privateId = 0;

    @PostConstruct
    void init() {
        refresh();
    }

    public int refresh() {
        return getId.get().orElse(0);
    }

    public int increase() {
        privateId++;
        return privateId;
    }

    public int id() {
        return privateId;
    }
}
