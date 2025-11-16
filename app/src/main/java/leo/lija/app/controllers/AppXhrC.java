package leo.lija.app.controllers;

import leo.lija.app.Hand;
import leo.lija.app.db.GameRepo;
import leo.lija.app.entities.DbGame;
import leo.lija.app.entities.event.Event;
import leo.lija.app.game.Socket;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.function.Function;

@RestController
public class AppXhrC extends BaseController {

    private final Hand hand;
    private final GameRepo gameRepo;
    private final Socket gameSocket;

    public AppXhrC(Hand hand, GameRepo gameRepo, @Qualifier("gameSocket") Socket gameSocket) {
        this.hand = hand;
        this.gameRepo = gameRepo;
        this.gameSocket = gameSocket;
    }

    @PostMapping("/outoftime/{fullId}")
    public void outoftime(@PathVariable String fullId) {
        perform(fullId, hand::outoftime);
    }

    @GetMapping("/abort/{fullId}")
    public ResponseEntity<Void> abort(@PathVariable String fullId) {
        return performAndRedirect(fullId, hand::abort);
    }

    @GetMapping("/resign/{fullId}")
    public ResponseEntity<Void> resign(@PathVariable String fullId) {
        return performAndRedirect(fullId, hand::resign);
    }

    @GetMapping("/resign-force/{fullId}")
    public ResponseEntity<Void> forceResign(@PathVariable String fullId) {
        return performAndRedirect(fullId, hand::forceResign);
    }

    @GetMapping("/draw-claim/{fullId}")
    public ResponseEntity<Void> drawClaim(@PathVariable String fullId) {
        return performAndRedirect(fullId, hand::drawClaim);
    }

    @GetMapping("/draw-accept/{fullId}")
    public ResponseEntity<Void> drawAccept(@PathVariable String fullId) {
        return performAndRedirect(fullId, hand::drawAccept);
    }

    @GetMapping("/draw-offer/{fullId}")
    public ResponseEntity<Void> drawOffer(@PathVariable String fullId) {
        return performAndRedirect(fullId, hand::drawOffer);
    }

    @GetMapping("/draw-cancel/{fullId}")
    public ResponseEntity<Void> drawCancel(@PathVariable String fullId) {
        return performAndRedirect(fullId, hand::drawCancel);
    }

    @GetMapping("/draw-decline/{fullId}")
    public ResponseEntity<Void> drawDecline(@PathVariable String fullId) {
        return performAndRedirect(fullId, hand::drawDecline);
    }

    @GetMapping({"/how-many-players-now", "/internal/nb-players"})
    public long nbPlayers() {
        return 0;
    }

    @GetMapping("/how-many-games-now")
    public int nbGames() {
        return gameRepo.countPlaying();
    }

    private void perform(String fullId, Function<String, List<Event>> op) {
        List<Event> events = op.apply(fullId);
        gameSocket.send(DbGame.takeGameId(fullId), events);
    }

    private ResponseEntity<Void> performAndRedirect(String fullId, Function<String, List<Event>> op) {
        perform(fullId, op);
        return ResponseEntity.status(HttpStatus.FOUND)
            .location(URI.create("/" + fullId))
            .build();
    }

}
