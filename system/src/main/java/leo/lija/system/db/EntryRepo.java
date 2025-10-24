package leo.lija.system.db;

import leo.lija.system.entities.Entry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class EntryRepo extends CappedRepo<Entry> {

    public EntryRepo(EntryRepoJpa repo, @Value("${lobby.entry.max}") int max) {
        super(repo, max);
    }

    public void add(Entry entry) {
        repo.save(entry);
    }
}
