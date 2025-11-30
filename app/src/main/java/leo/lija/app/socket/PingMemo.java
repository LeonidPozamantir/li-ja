package leo.lija.app.socket;

import leo.lija.app.memo.BooleanExpiryMemo;

public class PingMemo extends BooleanExpiryMemo {

    public PingMemo(int timeout) {
        super(timeout);
    }
}
