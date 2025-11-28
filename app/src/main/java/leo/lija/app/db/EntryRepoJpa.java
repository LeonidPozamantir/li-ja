package leo.lija.app.db;

import leo.lija.app.entities.Entry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntryRepoJpa extends JpaRepository<Entry, Integer> {
}
