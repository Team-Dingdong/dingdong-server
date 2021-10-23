package dingdong.dingdong.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AuthRepository extends JpaRepository<Auth, Long> {

    boolean existsByPhone(String phone);

    Optional<Auth> findByPhone(String phone);

    @Query("select a.requestTime from Auth a where a.phone = ?1")
    Optional<LocalDateTime> findRequestTimeByPhone(String phone);
}
