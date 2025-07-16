package leo.lija.system.entities;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@Getter
public class DbClock {

    private final String color;
    private final int increment;
    @Column(name = "time_limit")
    private final int limit;
    @ElementCollection
    private final Map<String, Float> times;
}
