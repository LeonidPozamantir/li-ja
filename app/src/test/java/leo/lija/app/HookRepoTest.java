package leo.lija.app;

import leo.lija.app.db.HookRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class HookRepoTest extends Fixtures {

    @Autowired
    private HookRepo repo;

    @Test
    @DisplayName("find no hook")
    void noHook() {
        repo.deleteAll();
        assertThat(repo.allOpen()).isEmpty();
    }
}
