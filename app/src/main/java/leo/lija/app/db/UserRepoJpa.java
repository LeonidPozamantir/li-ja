package leo.lija.app.db;

import leo.lija.app.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface UserRepoJpa extends JpaRepository<User, UUID> {

    Optional<User> findById(UUID id);

    @Query("select avg(u.elo) from User u")
    int averageElo();

    @Modifying
    @Query("update User set isOnline = true where username in :usernames and isOnline = false")
    @Transactional
    void updateOnlineUsernamesTrue(Collection<String> usernames);

    @Modifying
    @Query("update User set isOnline = false where username not in :usernames and isOnline = true")
    @Transactional
    void updateOnlineUsernamesFalse(Collection<String> usernames);

    Optional<User> findByUsernameCanonical(String username);

    @Modifying
    @Query("update User set elo = :elo where id = :id")
    @Transactional
    void setElo(UUID id, int elo);

    @Modifying
    @Query("update User set engine = true where id = :id")
    @Transactional
    void setEngine(UUID id);

    @Modifying
    @Query("update User set nbGames = nbGames + 1, nbRatedGames = nbRatedGames + 1 where id = :id")
    @Transactional
    void incRated(UUID id);

    @Modifying
    @Query("update User set nbGames = nbGames + 1 where id = :id")
    @Transactional
    void incNonRated(UUID id);
}
