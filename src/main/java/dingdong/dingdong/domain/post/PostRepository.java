package dingdong.dingdong.domain.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface PostRepository extends JpaRepository<Post, Long> {

    // 홈화면 최신순 정렬
    @Query(value = "select * from post, user where post.user_id = user.user_id AND (user.local1 = :local1 or user.local2 = :local2)",
            countQuery = "select count(*) from post",
            nativeQuery = true)
    Page<Post> findAllByCreateDate(Long local1,Long local2, Pageable pageable);

    // 홈화면 마감일자순 정렬
    @Query(value = "select * from post, user where post.user_id = user.user_id and (user.local1 = :local1 or user.local2 = :local2) " +
            "order by (post.gathered_people / post.people) desc",
            countQuery = "select count(*) from post",
            nativeQuery = true)
    Page<Post> findAllByEndDate(Long local1,Long local2, Pageable pageable);
    
    @Query(value = "select * from post, user where post.user_id = user.user_id and (user.local1 = :local1 or user.local2 = :local2) and post.category_id = :category_id",
            countQuery = "select count(*) from post",
            nativeQuery = true)
    Page<Post> findByCategoryId(Long local1, Long local2, Long category_id, Pageable pageable);

    @Query(value = "select * from post, user where post.user_id = user.user_id and (user.local1 = :local1 or user.local2 = :local2) and post.category_id = :category_id order by (post.gathered_people / post.people) desc",
            countQuery = "select count(*) from post",
            nativeQuery = true)
    Page<Post> findPostByCategoryIdSortByEndDate(Long local1, Long local2, Long category_id, Pageable pageable);

    Page<Post> findByUserId(Long UserId, Pageable pageable);

    @Query(value = "select * from post, user, post_tag, tag where post.user_id = user.user_id AND post.post_id = post_tag.post_id AND post_tag.tag_id = tag.tag_id AND" +
            "(tag.name LIKE %:keyword% )",
            countQuery = "select count(*) from post",
            nativeQuery = true)
    Page<Post> findAllSearchByTag(String keyword, Pageable pageable);

    @Query(value = "select * from post, user, category where post.user_id = user.user_id AND post.category_id = category.category_id AND (post.title LIKE %:keyword% OR  category.name LIKE %:keyword%)",
            countQuery = "select count(*) from post",
            nativeQuery = true)
    Page<Post> findAllSearch(String keyword, Pageable pageable);

    @Query(value = "select * from post, user, category where post.user_id = user.user_id AND post.category_id = category.category_id AND " +
            "(post.title LIKE %:keyword% OR  category.name LIKE %:keyword%)AND (user.local1 = :local1 or user.local2 = :local2)",
            countQuery = "select count(*) from post",
            nativeQuery = true)
    Page<Post> findAllSearchWithLocal(String keyword, Long local1,Long local2, Pageable pageable);

    // 검색 기능
    @Query(value = "select * from post, user, post_tag, tag where post.user_id = user.user_id AND post.post_id = post_tag.post_id AND post_tag.tag_id = tag.tag_id AND " +
            "(user.local1 = :local1 or user.local2 = :local2) AND (tag.name LIKE %:keyword% )",
            countQuery = "select count(*) from post",
            nativeQuery = true)
    Page<Post> findAllSearchByTagWithLocal(String keyword, Long local1,Long local2, Pageable pageable);

    @Query(value = "select * from post", countQuery = "select count(*) from post",
            nativeQuery = true)
    Page<Post> findAllByCreateDateNotLocal(Pageable pageable);

    @Query(value = "select * from post ORDER BY (post.gathered_people / post.people) desc", countQuery = "select count(*) from post",
            nativeQuery = true)
    Page<Post> findAllByEndDateNotLocal(Pageable pageable);

    @Query(value = "select * from post WHERE post.category_id = :category_id",
            countQuery = "select count(*) from post",
            nativeQuery = true)
    Page<Post> findPostByCategoryIdNotLocal(Long category_id, Pageable pageable);

    @Query(value = "select * from post WHERE post.category_id = :category_id order by (post.gathered_people / post.people) desc",
            countQuery = "select count(*) from post",
            nativeQuery = true)
    Page<Post> findPostByCategoryIdNotLocalSortByEndDate(Long category_id, Pageable pageable);
}
