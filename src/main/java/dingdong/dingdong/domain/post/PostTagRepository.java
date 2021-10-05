package dingdong.dingdong.domain.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {

    @Transactional
    void deleteByPost(Post post);

    @Query("select pt.tag from PostTag pt where pt.post = :post")
    List<Tag> findTagByPost(Post post);
}
