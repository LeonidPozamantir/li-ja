package leo.lija.app.db;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@RequiredArgsConstructor
public abstract class CappedRepo<T> {

    protected final JpaRepository<T, Integer> repo;
    protected final int max;

    public List<T> recent() {
        return repo.findAll(PageRequest.of(1, max, Sort.by(Sort.Direction.DESC, "id"))).toList();
    }

}
