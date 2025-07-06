package leo.lija.system;

import leo.lija.system.entities.DbGame;
import leo.lija.system.entities.Player;

import java.util.List;

public class Fixtures {

    Player white = newPlayer("white", "ip ar jp bn kp cb lp dq mp ek np fb op gn pp hr");
    Player black = newPlayer("black", "Wp 4r Xp 5n Yp 6b Zp 7q 0p 8k 1p 9b 2p !n 3p ?r");

    DbGame newDbGame = new DbGame(
        "arstdhne",
        List.of(white, black),
        "",
        10,
        0,
        1
        );

    Player newPlayer(String color, String ps) {
        return new Player(
            color.substring(0, 4),
            color,
            "ip ar jp bn kp cb lp dq mp ek np fb op gn pp hr",
            null,
            null,
            "0s|1Msystem White creates the game|2Msystem Black joins the game|3r/ipkkf590ldrr",
            1280
            );
    }
}
