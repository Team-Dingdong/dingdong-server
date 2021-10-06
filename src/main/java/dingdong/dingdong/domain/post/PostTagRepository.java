package dingdong.dingdong.domain.post;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {

    @Transactional
    @Modifying
    @Query(value = "delete from post_tag where post_id = :postId", nativeQuery = true)
    void deleteByPostId(@Param("postId") Long postId);

    @Query("select pt.tag from PostTag pt where pt.post = :post")
    List<Tag> findTagByPost(Post post);
}
