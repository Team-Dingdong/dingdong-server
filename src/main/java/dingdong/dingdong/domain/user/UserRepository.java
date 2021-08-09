package dingdong.dingdong.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByPhone(String phone);
    User findByPhone(String phone);
}
