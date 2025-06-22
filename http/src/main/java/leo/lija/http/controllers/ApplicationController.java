package leo.lija.http.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApplicationController {

    @GetMapping
    public String index() {
        return "Your new application is ready.";
    }
}
