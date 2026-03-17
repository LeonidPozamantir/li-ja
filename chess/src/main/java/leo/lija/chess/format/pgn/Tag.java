package leo.lija.chess.format.pgn;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class Tag {

    protected final String name;
    protected final String value;
}
