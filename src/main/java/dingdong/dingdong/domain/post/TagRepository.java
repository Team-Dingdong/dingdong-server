package dingdong.dingdong.domain.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    Tag findByName(String name);
    boolean existsByName(String name);
    void save(String name);

    @Query(value = "select CONCAT('#',tag.name) from post, post_tag, tag where post.post_id = post_tag.post_id AND post_tag.tag_id = tag.tag_id AND (post.post_id = :post_id)",
            nativeQuery = true)
    List<String> findAllByPost_Id(Long post_id);
}
