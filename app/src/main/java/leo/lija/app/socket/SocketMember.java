package leo.lija.app.socket;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
@Getter
public abstract class SocketMember {
    protected final String uid;
    protected final Optional<String> username;
}
