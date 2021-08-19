package dingdong.dingdong.domain.post;

import dingdong.dingdong.domain.user.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query(value = "select * from post, user where post.user_id = user.user_id and user.local1 = local1 or user.local2 = local2",
            countQuery = "select count(*) from post",
            nativeQuery = true)
    Page<Post> findAll(Long local1,Long local2, Pageable pageable); // findAll 페이징 처리

    @Query(value = "select * from post, user where post.user_id = user.user_id and user.local1 = local1 or user.local2 = local2 and post.category_id = category_id",
            countQuery = "select count(*) from post",
            nativeQuery = true)
    Page<Post> findByCategory_Id(Long local1, Long local2, Long category_id, Pageable pageable);

    Page<Post> findByUser_Id(Long UserID, Pageable pageable);
}
