package dingdong.dingdong.domain.chat;

import dingdong.dingdong.domain.user.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChatJoinRepository extends JpaRepository<ChatJoin, Long> {

    List<ChatJoin> findAllByUser(User user);

    List<ChatJoin> findAllByChatRoom(ChatRoom chat);

    Optional<ChatJoin> findByChatRoomAndUser(ChatRoom chatRoom, User user);

    boolean existsByChatRoomAndUser(ChatRoom chatRoom, User user);

    @Query(value = "select distinct user_id from chat_join where post_id in (select post_id from chat_join where user_id = :userId)", nativeQuery = true)
    List<Long> existsUserByUser(Long userId);
}
