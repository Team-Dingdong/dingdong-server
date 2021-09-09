package dingdong.dingdong.domain.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface PostTagRepository extends JpaRepository<PostTag, Long> {

    @Query("select pt.tag from PostTag pt where pt.post = :post")
    List<Tag> findTagByPost(Post post);
    void deleteByPost(Post post);
}
