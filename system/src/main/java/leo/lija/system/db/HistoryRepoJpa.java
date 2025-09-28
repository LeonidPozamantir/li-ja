package leo.lija.system.db;

import leo.lija.system.entities.DbHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepoJpa extends JpaRepository<DbHistory, String> {
}
