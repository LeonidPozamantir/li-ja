package leo.lija.system.db;

import leo.lija.system.entities.Entry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntryRepoJpa extends JpaRepository<Entry, Integer> {
}
