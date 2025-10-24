package leo.lija.app.db;

import leo.lija.app.entities.DbHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepoJpa extends JpaRepository<DbHistory, String> {
}
