package dingdong.dingdong.domain.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface TagRepository extends JpaRepository<Tag, Long> {

    Tag findByName(String name);
    boolean existsByName(String name);
    void save(String name);
}
