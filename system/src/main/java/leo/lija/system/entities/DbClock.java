package leo.lija.system.entities;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@Getter
public class DbClock {

    private String color;
    private Integer increment;
    @Column(name = "time_limit")
    private Integer limit;
    @ElementCollection
    private Map<String, Float> times;
}
