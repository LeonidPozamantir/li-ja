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

    public Optional<User> findById(UUID id);

    @Modifying
    @Query("update User set isOnline = true where username in :usernames and isOnline = false")
    @Transactional
    public void updateOnlineUsernamesTrue(Collection<String> usernames);

    @Modifying
    @Query("update User set isOnline = false where username not in :usernames and isOnline = true")
    @Transactional
    public void updateOnlineUsernamesFalse(Collection<String> usernames);

    @Modifying
    @Query("update User set elo = :elo where id = :id")
    @Transactional
    public void setElo(UUID id, int elo);

    @Modifying
    @Query("update User set nbGames = nbGames + 1, nbRatedGames = nbRatedGames + 1 where id = :id")
    @Transactional
    public void incRated(UUID id);

    @Modifying
    @Query("update User set nbGames = nbGames + 1 where id = :id")
    @Transactional
    public void incNonRated(UUID id);
}
