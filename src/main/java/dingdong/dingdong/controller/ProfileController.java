package dingdong.dingdong.controller;

import dingdong.dingdong.domain.user.CurrentUser;
import dingdong.dingdong.domain.user.User;
import dingdong.dingdong.dto.profile.ProfileResponseDto;
import dingdong.dingdong.dto.profile.ProfileUpdateRequestDto;
import dingdong.dingdong.dto.profile.ReportRequestDto;
import dingdong.dingdong.service.profile.ProfileService;
import dingdong.dingdong.util.exception.Result;
import dingdong.dingdong.util.exception.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<Result> updateProfile(@CurrentUser User user,
        @ModelAttribute ProfileUpdateRequestDto profileUpdateRequestDto) {
        profileService.updateProfile(user, profileUpdateRequestDto);
        return Result.toResult(ResultCode.PROFILE_UPDATE_SUCCESS);
    }

    // 신고하기
    @PostMapping("/report/{userId}")
    public ResponseEntity<Result> createReport(@CurrentUser User user, @PathVariable Long userId,
        @RequestBody ReportRequestDto reportRequestDto) {
        profileService.createReport(user, userId, reportRequestDto);
        return Result.toResult(ResultCode.REPORT_CREATE_SUCCESS);
    }
}
