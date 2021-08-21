package dingdong.dingdong.controller;

import dingdong.dingdong.domain.user.CurrentUser;
import dingdong.dingdong.domain.user.User;
import dingdong.dingdong.dto.profile.ProfileResponseDto;
import dingdong.dingdong.dto.profile.ProfileUpdateRequestDto;
import dingdong.dingdong.service.profile.ProfileService;
import dingdong.dingdong.util.exception.Result;
import dingdong.dingdong.util.exception.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {

    private final ProfileService profileService;

    // 본인 프로필 조회
    @GetMapping("")
    public ResponseEntity<Result<ProfileResponseDto>> getMyProfile(@CurrentUser User user) {
        Long id = user.getId();
        ProfileResponseDto data = profileService.getProfile(id);
        return Result.toResult(ResultCode.PROFILE_READ_SUCCESS, data);
    }

    // 프로필 조회
    @GetMapping("/{userId}")
    public ResponseEntity<Result<ProfileResponseDto>> getProfile(@PathVariable Long userId) {
        ProfileResponseDto data = profileService.getProfile(userId);
        return Result.toResult(ResultCode.PROFILE_READ_SUCCESS, data);
    }

    // 프로필 수정
    @PatchMapping("")
    public ResponseEntity<Result> updateProfile(@CurrentUser User user, @ModelAttribute ProfileUpdateRequestDto profileUpdateRequestDto) throws IOException {
        profileService.updateProfile(user, profileUpdateRequestDto);
        return Result.toResult(ResultCode.PROFILE_UPDATE_SUCCESS);
    }

}
