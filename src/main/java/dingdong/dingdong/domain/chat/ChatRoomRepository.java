package dingdong.dingdong.domain.chat;

import dingdong.dingdong.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
@Transactional
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByPostId(Long postId);
    boolean existsByPostId(Long postId);
    void deleteByPost(Post post);
}
