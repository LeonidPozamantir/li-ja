package leo.lija.app.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String username;
    private String usernameCanonical;
    private boolean isOnline;
    private int elo;
    private int nbGames;
    private int nbRatedGames;
    private boolean engine;
    private boolean isChatBan;
    private boolean enabled;
    private List<String> roles;
    private String password;
    private String salt;

    public boolean disabled() {
        return !enabled;
    }

    public static final int STARTING_ELO = 1200;

}
