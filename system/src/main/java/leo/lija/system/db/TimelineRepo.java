package leo.lija.system.db;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface TimelineRepo<T> extends JpaRepository<T, Integer> {

    @Query("select max(e.id) from #{#entityName} e")
    Optional<Integer> lastId();

    default List<T> recent(int max) {
        return findByOrderByIdDesc(Limit.of(max));
    }

    List<T> findByOrderByIdDesc(Limit limit);

    @Query("select e from #{#entityName} e where id > :id order by id desc")
    List<T> since(int id);
}
