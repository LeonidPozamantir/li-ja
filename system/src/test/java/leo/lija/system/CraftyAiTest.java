package leo.lija.system;

import leo.lija.system.ai.CraftyAi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CraftyAiTest extends AiTest {

    CraftyAiTest(@Autowired CraftyAi craftyAi) {
        this.ai = craftyAi;
        this.name = "crafty";
        this.nbMoves = 5;
    }

}
