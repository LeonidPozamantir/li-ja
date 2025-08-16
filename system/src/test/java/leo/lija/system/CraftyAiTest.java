package leo.lija.system;

import leo.lija.system.ai.CraftyAi;

public class CraftyAiTest extends AiTest {

    CraftyAiTest() {
        this.ai = new CraftyAi();
        this.name = "crafty";
        this.nbMoves = 5;
    }

}
