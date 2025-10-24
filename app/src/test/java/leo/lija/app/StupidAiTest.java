package leo.lija.app;

import leo.lija.app.ai.StupidAi;

public class StupidAiTest extends AiTest {

    StupidAiTest() {
        this.ai = new StupidAi();
        this.name = "stupid";
        this.nbMoves = 10;
    }

}
