package dingdong.dingdong.service.profile;

import dingdong.dingdong.domain.user.Profile;
import dingdong.dingdong.domain.user.ProfileRepository;
import dingdong.dingdong.domain.user.User;
import dingdong.dingdong.dto.profile.ProfileResponseDto;
import dingdong.dingdong.util.exception.ResourceNotFoundException;
import dingdong.dingdong.util.exception.ResultCode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Data
@Service
public class ProfileService {

    private final ProfileRepository profileRepository;

    @Transactional
    public ProfileResponseDto getProfile(User user) {
        Profile profile = profileRepository.findByUserId(user.getId()).orElseThrow(() -> new ResourceNotFoundException(ResultCode.PROFILE_NOT_FOUND));
        return ProfileResponseDto.from(profile);
    }
}
