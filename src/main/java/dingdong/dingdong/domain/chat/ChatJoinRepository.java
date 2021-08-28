package dingdong.dingdong.domain.chat;

import dingdong.dingdong.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatJoinRepository extends JpaRepository<ChatJoin, Long> {
    List<ChatJoin> findAllByUser(User user);
}
