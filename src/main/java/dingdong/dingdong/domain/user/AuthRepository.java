package dingdong.dingdong.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<Auth, Long> {
    boolean existsByPhone(String Phone);
    Auth findByPhone(String phone);
    Auth findByRequestId(String requestId);
}
