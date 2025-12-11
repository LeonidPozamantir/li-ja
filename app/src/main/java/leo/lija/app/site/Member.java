package leo.lija.app.site;

import leo.lija.app.socket.SocketMember;

import java.util.Optional;

public class Member extends SocketMember {
    public Member(String uid, Optional<String> username) {
        super(uid, username);
    }
}
