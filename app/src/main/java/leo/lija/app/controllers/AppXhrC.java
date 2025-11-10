package leo.lija.app.controllers;

import jakarta.validation.Valid;
import leo.lija.app.AppXhr;
import leo.lija.app.db.GameRepo;
import leo.lija.app.forms.MoveForm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@RestController
@RequiredArgsConstructor
public class AppXhrC extends BaseController {

    private final AppXhr xhr;
    private final GameRepo gameRepo;

    @PostMapping("/move/{fullId}")
    public ResponseEntity<String> move(@PathVariable String fullId, @Valid @RequestBody MoveForm move, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("Invalid move");
        }

        return CompletableFuture.supplyAsync(() -> {
            xhr.play(fullId, move.from(), move.to(), Optional.ofNullable(move.promotion()));
            return ResponseEntity.ok().body("ok");
        }).join();

    }

    @PostMapping("/outoftime/{fullId}")
    public void outoftime(@PathVariable String fullId) {
        xhr.outoftime(fullId);
    }

    @GetMapping("/abort/{fullId}")
    public ResponseEntity<Void> abort(@PathVariable String fullId) {
        return validAndRedirect(fullId, xhr::abort);
    }

    @GetMapping("/resign/{fullId}")
    public ResponseEntity<Void> resign(@PathVariable String fullId) {
        return validAndRedirect(fullId, xhr::resign);
    }

    @GetMapping("/resign-force/{fullId}")
    public ResponseEntity<Void> forceResign(@PathVariable String fullId) {
        return validAndRedirect(fullId, xhr::forceResign);
    }

    @GetMapping("/draw-claim/{fullId}")
    public ResponseEntity<Void> drawClaim(@PathVariable String fullId) {
        return validAndRedirect(fullId, xhr::drawClaim);
    }

    @GetMapping("/draw-accept/{fullId}")
    public ResponseEntity<Void> drawAccept(@PathVariable String fullId) {
        return validAndRedirect(fullId, xhr::drawAccept);
    }

    @GetMapping("/draw-offer/{fullId}")
    public ResponseEntity<Void> drawOffer(@PathVariable String fullId) {
        return validAndRedirect(fullId, xhr::drawOffer);
    }

    @GetMapping("/draw-cancel/{fullId}")
    public ResponseEntity<Void> drawCancel(@PathVariable String fullId) {
        return validAndRedirect(fullId, xhr::drawCancel);
    }

    @GetMapping("/draw-decline/{fullId}")
    public ResponseEntity<Void> drawDecline(@PathVariable String fullId) {
        return validAndRedirect(fullId, xhr::drawDecline);
    }

    @PostMapping("/moretime/{fullId}")
    public float moretime(@PathVariable String fullId) {
        return xhr.moretime(fullId);
    }

    @GetMapping({"/how-many-players-now", "/internal/nb-players"})
    public long nbPlayers() {
        return 0;
    }

    @GetMapping("/how-many-games-now")
    public int nbGames() {
        return gameRepo.countPlaying();
    }

    private ResponseEntity<Void> validAndRedirect(String fullId, Consumer<String> f) {
        return validRedir(() -> f.accept(fullId), fullId);
    }

}
