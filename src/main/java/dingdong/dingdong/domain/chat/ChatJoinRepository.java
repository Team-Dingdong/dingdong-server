package dingdong.dingdong.domain.chat;

import dingdong.dingdong.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface ChatJoinRepository extends JpaRepository<ChatJoin, Long> {
    List<ChatJoin> findAllByUser(User user);
    List<ChatJoin> findAllByChatRoom(ChatRoom chat);
    Optional<ChatJoin> findByChatRoomAndUser(ChatRoom chatRoom, User user);
    boolean existsByChatRoomAndUser(ChatRoom chatRoom, User user);

    @Query(value = "select * from chat_join where chat_join.post_id = :id", nativeQuery = true)
    List<ChatJoin> findAllByPost_Id(Long id);

    @Modifying
    @Query(value = "delete from chat_join where chat_join.post_id = :post_id", nativeQuery = true)
    void deleteByPost_id(Long post_id);
}
