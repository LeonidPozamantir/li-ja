package leo.lija.system.memo;

import jakarta.annotation.PostConstruct;
import leo.lija.system.exceptions.AppException;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class MessageMemo {

    private final Supplier<Optional<Integer>> getId;

    private int privateId = 0;

    @PostConstruct
    void init() {
        try {
            refresh();
        } catch (Exception e) {
            // do nothing
        }
    }

    public int refresh() {
        privateId = getId.get().orElseThrow(() -> new AppException("No last message found"));
        return privateId;
    }

    public int increase() {
        privateId++;
        return privateId;
    }

    public int id() {
        return privateId;
    }
}
