package dingdong.dingdong.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<Auth, Long> {
    Auth findByRequestId(String requestId);
}
