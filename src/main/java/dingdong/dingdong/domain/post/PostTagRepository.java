package dingdong.dingdong.domain.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostTagRepository extends JpaRepository<PostTag, Long> {
}
