package dingdong.dingdong.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByPhone(String phone);

    User findByPhone(String phone);

    @Modifying
    @Query(value = "delete from user where deleted_date + INTERVAL 12 DAY <= now()", nativeQuery = true)
    void deleteUnsubUser();
}
