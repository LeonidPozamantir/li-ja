package leo.lija.cli;

import leo.lija.app.db.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Eco implements Command {

    private final UserRepo userRepo;

    @Override
    public void apply() {
        System.out.println(leo.lija.chess.Eco.getTree().render());
    }
}
