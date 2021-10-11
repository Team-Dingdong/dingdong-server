package dingdong.dingdong.domain.chat;

import dingdong.dingdong.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatPromiseVoteRepository extends JpaRepository<ChatPromiseVote, Long> {

    List<ChatPromiseVote> findAllByChatRoom(ChatRoom chatRoom);

    boolean existsByChatRoomAndUser(ChatRoom chatRoom, User user);
}
