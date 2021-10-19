package dingdong.dingdong.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByPhone(String phone);

    User findByPhone(String phone);

    @Modifying
    @Query(value = "update user set phone = null, modified_date = now() where deleted_date + INTERVAL 12 DAY <= now()", nativeQuery = true)
    void deleteUnsubUser();

    @Modifying
    @Query(value = "update user set authority = 1, modified_date = now() where authority = 2 and modified_date + INTERVAL 14 DAY <= now()", nativeQuery = true)
    void derestrictStoppedUser();
}
