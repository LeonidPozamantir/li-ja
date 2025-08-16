package leo.lija.system;

import leo.lija.system.ai.StupidAi;

public class StupidAiTest extends AiTest {

    StupidAiTest() {
        this.ai = new StupidAi();
        this.name = "stupid";
        this.nbMoves = 10;
    }

}
