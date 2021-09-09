package dingdong.dingdong.domain.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

}
