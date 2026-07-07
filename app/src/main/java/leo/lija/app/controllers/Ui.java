package leo.lija.app.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ui")
public class Ui {

    @GetMapping("/main")
    public String main(Authentication auth) {
        return auth == null ? null : auth.getName();
    }
}
