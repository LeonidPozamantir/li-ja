package leo.lija.app.entities.event;

public abstract class EmptyEvent implements Event {

    @Override
    public Object data() {
        return null;
    }
}
