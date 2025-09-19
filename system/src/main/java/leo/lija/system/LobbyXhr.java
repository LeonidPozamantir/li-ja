package leo.lija.system;

import leo.lija.system.db.GameRepo;
import leo.lija.system.memo.AliveMemo;
import leo.lija.system.memo.VersionMemo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LobbyXhr {

    private final GameRepo gameRepo;
    private final VersionMemo versionMemo;
    private final AliveMemo aliveMemo;
}
