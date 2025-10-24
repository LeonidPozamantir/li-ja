package leo.lija.app;

import leo.lija.app.ai.CraftyAi;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled
public class CraftyAiTest extends AiTest {

    CraftyAiTest(@Autowired CraftyAi craftyAi) {
        this.ai = craftyAi;
        this.name = "crafty";
        this.nbMoves = 5;
    }

}
