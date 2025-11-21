package leo.lija.app.db;

import leo.lija.app.entities.User;
import leo.lija.app.exceptions.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepo {

    private final UserRepoJpa repo;

    public void updateOnlineUserNames(Collection<String> usernames) {
        List<String> names = usernames.stream().map(String::toLowerCase).distinct().toList();
        repo.updateOnlineUsernamesFalse(names);
        repo.updateOnlineUsernamesTrue(names);
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
