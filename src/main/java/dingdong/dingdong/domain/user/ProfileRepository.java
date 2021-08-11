package dingdong.dingdong.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    boolean existsByNickname(String nickname);
}
