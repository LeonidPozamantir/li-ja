package leo.lija.chess;

import leo.lija.chess.exceptions.ChessRulesException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static leo.lija.chess.Role.QUEEN;

@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Situation {

	@Getter
	@EqualsAndHashCode.Include
	final Board board;
	@Getter
	@EqualsAndHashCode.Include
	final Color color;

	private Optional<Map<Pos, List<Pos>>> cachedDestinations = Optional.empty();

	public Situation() {
		this(new Board(), Color.WHITE);
	}

	public List<Actor> actors() {
		return board.actorsOf(color);
	}

	public Map<Pos, List<Move>> moves() {
		return actors().stream()
			.filter(a -> !a.destinations().isEmpty())
			.collect(Collectors.toMap(Actor::getPos, Actor::moves));
	}

	public Map<Pos, List<Pos>> destinations() {
		if (cachedDestinations.isEmpty()) {
			Map<Pos, List<Pos>> dests = moves().entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream().map(m -> m.dest()).toList()));
			cachedDestinations = Optional.of(dests);
		}
		return cachedDestinations.get();
	}

	public Optional<Pos> kingPos() {
		return board.kingPosOf(color);
	}

	public boolean check() {
		return board.check(color);
	}

	public boolean checkmate() {
		return check() && moves().isEmpty();
	}

	public boolean stalemate() {
		return !check() && moves().isEmpty();
	}

	public boolean autoDraw() {
		return board.autodraw();
	}

	public boolean threefoldRepetition() {
		return board.getHistory().threefoldRepetition();
	}

	public boolean end() {
		return checkmate() || stalemate() || autoDraw();
	}

	public Move move(Pos from, Pos to, Optional<Role> promotion) {
		if (promotion.isPresent() && !promotion.get().isPromotable) throw new ChessRulesException("Cannot promote to %s".formatted(promotion));

		return board.actorAt(from)
            .filter(a -> a.is(color))
            .flatMap(a -> a.moves().stream()
                .filter(m -> m.dest().equals(to))
                .findFirst()
            ).flatMap(m1 -> m1.setPromotion(promotion))
			.orElseThrow(() -> new ChessRulesException("Illegal move %s->%s".formatted(from, to)));
	}

}
