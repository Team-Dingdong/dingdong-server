package dingdong.dingdong.domain.chat;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ChatPromiseRepository extends JpaRepository<ChatPromise, Long> {

    Optional<ChatPromise> findByChatRoomId(Long id);

    boolean existsByChatRoomId(Long id);

    @Modifying
    @Query(value = "update chat_promise set type = 1 where promise_end_time <= now() AND type = 0 ", nativeQuery = true)
    void updateByLocalDateTime();
}
