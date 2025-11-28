package leo.lija.app.controllers;

import java.util.Optional;

public class BaseController {

    protected Optional<String> get(Optional<String> s) {
        return s.filter(v -> !v.isEmpty());
    }
}
