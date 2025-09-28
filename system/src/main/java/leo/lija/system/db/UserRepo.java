package leo.lija.system.db;

import leo.lija.system.entities.User;
import leo.lija.system.exceptions.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepo {

    private final UserRepoJpa repo;

    public void updateOnlineUserNames(Collection<String> usernames) {
        repo.updateOnlineUsernamesFalse(usernames);
        repo.updateOnlineUsernamesTrue(usernames);
    }

    public User user(String userId) {
        return repo.findById(UUID.fromString(userId)).orElseThrow(() -> new AppException("No user found for id " + userId));
    }

    public void setElo(String userId, int elo) {
        repo.setElo(UUID.fromString(userId), elo);
    }

    public void incNbGames(String userId, boolean rated) {
        if (rated) repo.incRated(UUID.fromString(userId));
        else repo.incNonRated(UUID.fromString(userId));
    }
}
