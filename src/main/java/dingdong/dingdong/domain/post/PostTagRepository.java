package dingdong.dingdong.domain.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface PostTagRepository extends JpaRepository<PostTag, Long> {

    List<PostTag> findAllByPost(Post post);

    void deleteByPost(Post post);
}
