package leo.lija.model;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
class Trajectory {

    private final List<Function<Pos, Optional<Pos>>> dirs;
    private final Set<Pos> friends;
    private final Set<Pos> enemies;

    public Set<Pos> from(Pos pos) {
        return dirs.stream().flatMap(dir -> forward(pos, dir).stream()).collect(Collectors.toSet());
    }

    public List<Pos> forward(Pos p, Function<Pos, Optional<Pos>> dir) {
        List<Pos> res = new ArrayList<>();
        Optional<Pos> next = dir.apply(p);
        while (next.isPresent() && !friends.contains(next.get())) {
            res.add(next.get());
            if (enemies.contains(next.get())) break;
            next = dir.apply(next.get());
        }
        return res;
    }
}
