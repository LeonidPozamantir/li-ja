package leo.lija.app.entities;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DbHistory {

    @EmbeddedId
    private DbHistoryKey id;

    int t;
    int elo;
    String g;



    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Embeddable
    public static class DbHistoryKey implements Serializable {
        String id;
        String tsKey;
    }
}
