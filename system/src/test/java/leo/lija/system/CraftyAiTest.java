package leo.lija.system;

import leo.lija.system.ai.CraftyAi;
import leo.lija.system.config.CraftyConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CraftyAiTest extends AiTest {

    CraftyAiTest(@Autowired CraftyConfig config) {
        this.ai = new CraftyAi(config);
        this.name = "crafty";
        this.nbMoves = 5;
    }

}
