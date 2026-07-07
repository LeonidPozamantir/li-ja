package leo.lija.app;

import java.util.List;

public interface HubRef {
    int getNbMembers();
    void nbMembers(int nb);
    List<String> getUsernames();
}
