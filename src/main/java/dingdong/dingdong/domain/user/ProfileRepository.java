package dingdong.dingdong.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    boolean existsByNickname(String nickname);
    Optional<Profile> findByUserId(Long userId);
}
