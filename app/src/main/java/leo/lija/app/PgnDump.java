package leo.lija.app;

import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import leo.lija.app.db.GameRepo;
import leo.lija.app.db.UserRepo;
import leo.lija.app.entities.DbGame;
import leo.lija.app.entities.DbPlayer;
import leo.lija.app.entities.User;
import leo.lija.chess.utils.Pair;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PgnDump {

    private final UserRepo userRepo;
    private final GameRepo gameRepo;

    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public String game2Str(DbGame game) {
        return "%s\n\n%s %s".formatted(header(game), moves(game), result(game));
    }

    public String header(DbGame game) {
        Optional<User> whiteUser = user(game.getWhitePlayer());
        Optional<User> blackUser = user(game.getBlackPlayer());
        Optional<String> initialFen = game.getVariant().isStandard()
            ? Optional.empty()
            : gameRepo.initialFen(game.getId());
        return Streams.concat(
            Stream.of(
                Pair.of("Event", game.rated() ? "Rated game" : "Casual game"),
                Pair.of("Site", "http://leochess.com/" + game.getId()),
                Pair.of("Date", game.getCreatedAt().map(dateFormat::format).orElse("?")),
                Pair.of("White", player(game.getWhitePlayer(), whiteUser)),
                Pair.of("Black", player(game.getBlackPlayer(), blackUser)),
                Pair.of("WhiteElo", elo(game.getWhitePlayer())),
                Pair.of("BlackElo", elo(game.getBlackPlayer())),
                Pair.of("Result", result(game)),
                Pair.of("PlayCount", String.valueOf(game.getTurns())),
                Pair.of("Variant", game.getVariant().name())
            ),
            game.getVariant().isStandard()
                ? Stream.of()
                : Stream.of(
                Pair.of("FEN", initialFen.orElse("?")),
                Pair.of("SetUp", "1")
            )
        ).map(p -> "[%s %s]".formatted(p.getFirst(), p.getSecond()))
            .collect(Collectors.joining("\n"));
    }

    public String elo(DbPlayer p) {
        return p.getElo().map(String::valueOf).orElse("?");
    }

    public Optional<User> user(DbPlayer p) {
        return p.getUserId().flatMap(userRepo::user);
    }

    public String player(DbPlayer p, Optional<User> u) {
        return p.getAiLevel().map(aiLevel -> "Crafty level " + aiLevel)
            .orElse(
                u.map(User::getUsername)
                    .orElse("Anonymous")
            );
    }

    public String moves(DbGame game) {
        List<String> movesStrings = Lists.partition(game.pgnList(), 2).stream()
            .map(movesList -> String.join(" ", movesList))
            .toList();
        return IntStream.rangeClosed(1, movesStrings.size())
            .mapToObj(i -> i + ". " + movesStrings.get(i - 1))
            .collect(Collectors.joining(" "));
    }

    public String result(DbGame game) {
        if (game.finished())
            return game.winnerColor()
                .map(c -> c.isWhite() ? "1-0" : "0-1")
                .orElse("1/2-1/2");
        return "*";
    }
}
