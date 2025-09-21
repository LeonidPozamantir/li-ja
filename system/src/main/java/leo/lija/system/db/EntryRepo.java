package leo.lija.system.db;

import leo.lija.system.entities.entry.Entry;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EntryRepo extends JpaRepository<Entry, Integer> {

    @Query("select max(e.id) from Entry e")
    Optional<Integer> lastId();

    default List<Entry> recent(int max) {
        return findByOrderByIdDesc(Limit.of(max));
    }

    List<Entry> findByOrderByIdDesc(Limit limit);

    @Query("select e from Entry e where id > :id order by id desc")
    List<Entry> since(int id);
}
