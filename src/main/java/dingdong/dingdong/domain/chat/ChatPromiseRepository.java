package dingdong.dingdong.domain.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface ChatPromiseRepository extends JpaRepository<ChatPromise, Long> {

    Optional<ChatPromise> findByChatRoom(ChatRoom chatRoom);
    void deleteById(Long id);

    @Modifying
    @Query(value = "update chat_promise set type = 'END' where promise_end_time <= now()", nativeQuery = true)
    void updateByLocalDateTime();

}
