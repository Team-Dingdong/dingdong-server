package dingdong.dingdong.service.profile;

import dingdong.dingdong.domain.user.Profile;
import dingdong.dingdong.domain.user.ProfileRepository;
import dingdong.dingdong.domain.user.User;
import dingdong.dingdong.dto.profile.ProfileResponseDto;
import dingdong.dingdong.dto.profile.ProfileUpdateRequestDto;
import dingdong.dingdong.service.s3.S3Uploader;
import dingdong.dingdong.util.exception.DuplicateException;
import dingdong.dingdong.util.exception.ResourceNotFoundException;
import dingdong.dingdong.util.exception.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static dingdong.dingdong.util.exception.ResultCode.PROFILE_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final S3Uploader s3Uploader;

    // 프로필 조회
    @Transactional
    public ProfileResponseDto getProfile(Long id) {
        Profile profile = profileRepository.findByUserId(id).orElseThrow(() -> new ResourceNotFoundException(ResultCode.PROFILE_NOT_FOUND));

        return ProfileResponseDto.from(profile);
    }

    // 닉네임 중복 확인
    @Transactional
    public void checkNickname(String nickname) {
        if(profileRepository.existsByNickname(nickname)) {
            throw new DuplicateException(ResultCode.NICKNAME_DUPLICATION);
        }
    }

    // 프로필 수정
    @Transactional
    public void updateProfile(User user, ProfileUpdateRequestDto profileUpdateRequestDto) throws IOException {
        Profile profile = profileRepository.findById(user.getId()).orElseThrow(() -> new ResourceNotFoundException(PROFILE_NOT_FOUND));

        if(profileUpdateRequestDto.getProfileImage() != null) {
            String path = s3Uploader.upload(profileUpdateRequestDto.getProfileImage(), "static");
            profile.setProfileImageUrl(path);
        }
        if(profileUpdateRequestDto.getNickname() != null) {
            checkNickname(profileUpdateRequestDto.getNickname());
            profile.setNickname(profileUpdateRequestDto.getNickname());
        }

        profileRepository.save(profile);
    }
}
