package dingdong.dingdong.domain.chat;

import dingdong.dingdong.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatPromiseVoteRepository extends JpaRepository<ChatPromiseVote, Long> {

    List<ChatPromiseVote> findAllByChatRoom(ChatRoom chatRoom);
    boolean existsByChatRoomAndUser(ChatRoom chatRoom, User user);

    @Query(value = "select IF(COUNT(*) > 0, 'TRUE', 'FALSE') from chat_promise_vote where chat_promise_vote.room_id = :post_id and chat_promise_vote.user_id = :user_id",
    nativeQuery = true)
    boolean ExistsByRoomAndUser(Long post_id, Long user_id);

}
