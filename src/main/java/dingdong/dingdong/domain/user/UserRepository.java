package dingdong.dingdong.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByPhone(String phone);
    User findByPhone(String phone);
}
