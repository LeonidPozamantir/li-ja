package leo.lija.system.db;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
@RequiredArgsConstructor
public class UserRepo {

    private final UserRepoJpa repo;

    public void updateOnlineUserNames(Collection<String> usernames) {
        repo.updateOnlineUsernamesFalse(usernames);
        repo.updateOnlineUsernamesTrue(usernames);
    }
}
