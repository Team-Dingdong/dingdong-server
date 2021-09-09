package dingdong.dingdong.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    boolean existsByNickname(String nickname);
    Optional<Profile> findByUserId(Long userId);
}
