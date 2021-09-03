package dingdong.dingdong.domain.chat;

import dingdong.dingdong.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatJoinRepository extends JpaRepository<ChatJoin, Long> {
    List<ChatJoin> findAllByUser(User user);
    List<ChatJoin> findAllByChatRoom(ChatRoom chat);
    Optional<ChatJoin> findByChatRoomAndUser(ChatRoom chatRoom, User user);
    boolean existsByChatRoomAndUser(ChatRoom chatRoom, User user);
}
