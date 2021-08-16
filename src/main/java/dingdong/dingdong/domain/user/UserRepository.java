package dingdong.dingdong.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByPhone(String phone);
    User findByPhone(String phone);
    Optional<User> findById(Long id);
}
