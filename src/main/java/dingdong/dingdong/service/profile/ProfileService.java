package dingdong.dingdong.service.profile;

import static dingdong.dingdong.util.exception.ResultCode.PROFILE_NOT_FOUND;

import dingdong.dingdong.domain.user.BlackList;
import dingdong.dingdong.domain.user.BlackListRepository;
import dingdong.dingdong.domain.user.Profile;
import dingdong.dingdong.domain.user.ProfileRepository;
import dingdong.dingdong.domain.user.RefreshTokenRepository;
import dingdong.dingdong.domain.user.Report;
import dingdong.dingdong.domain.user.ReportRepository;
import dingdong.dingdong.domain.user.User;
import dingdong.dingdong.domain.user.UserRepository;
import dingdong.dingdong.dto.auth.LocalResponseDto;
import dingdong.dingdong.dto.profile.ProfileResponseDto;
import dingdong.dingdong.dto.profile.ProfileUpdateRequestDto;
import dingdong.dingdong.dto.profile.ReportRequestDto;
import dingdong.dingdong.service.s3.S3Uploader;
import dingdong.dingdong.util.exception.DuplicateException;
import dingdong.dingdong.util.exception.ForbiddenException;
import dingdong.dingdong.util.exception.ResourceNotFoundException;
import dingdong.dingdong.util.exception.ResultCode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final BlackListRepository blackListRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final S3Uploader s3Uploader;

    private static final long LIMIT_REPORT_COUNT_STOPPED = 2;
    private static final long LIMIT_REPORT_COUNT_BLACK = 4;

    // 프로필 조회
    @Transactional(readOnly = true)
    public ProfileResponseDto getProfile(Long id) {
        Profile profile = profileRepository.findByUserId(id)
            .orElseThrow(() -> new ResourceNotFoundException(ResultCode.PROFILE_NOT_FOUND));

        return ProfileResponseDto.from(profile);
    }

    // 닉네임 중복 확인
    @Transactional(readOnly = true)
    public void checkNickname(String nickname) {
        if (profileRepository.existsByNickname(nickname)) {
            throw new DuplicateException(ResultCode.NICKNAME_DUPLICATION);
        }
    }

    // 프로필 수정
    @Transactional
    public void updateProfile(User user, ProfileUpdateRequestDto profileUpdateRequestDto) {
        Profile profile = profileRepository.findById(user.getId())
            .orElseThrow(() -> new ResourceNotFoundException(PROFILE_NOT_FOUND));

        if (profileUpdateRequestDto.getProfileImage() != null) {
            String path = s3Uploader.upload(profileUpdateRequestDto.getProfileImage(), "static");
            profile.setProfileImageUrl(path);
        }
        if (profileUpdateRequestDto.getNickname() != null) {
            if (user.getProfile().getNickname() != null && user.getProfile().getNickname()
                .equals(profileUpdateRequestDto.getNickname())) {
                return;
            }
            checkNickname(profileUpdateRequestDto.getNickname());
            profile.setNickname(profileUpdateRequestDto.getNickname());
        }

        profileRepository.save(profile);
    }

    // 나의 동네 조회
    @Transactional
    public List<LocalResponseDto> getMyLocals(User user) {
        return List.of(user.getLocal1(), user.getLocal2()).stream().map(LocalResponseDto::from)
            .collect(Collectors.toList());
    }

    // 신고하기
    @Transactional
    public void createReport(User sender, Long userId, ReportRequestDto reportRequestDto) {
        User receiver = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(ResultCode.USER_NOT_FOUND));

        if (sender.getId() == receiver.getId()) {
            throw new ForbiddenException(ResultCode.REPORT_CREATE_FAIL_SELF);
        }

        if (reportRepository.existsBySenderAndReceiver(sender, receiver)) {
            throw new DuplicateException(ResultCode.REPORT_DUPLICATION);
        }

        Report report = Report.builder()
            .sender(sender)
            .receiver(receiver)
            .reportDate(LocalDateTime.now())
            .reason(reportRequestDto.getReason())
            .build();

        reportRepository.save(report);

        // 신고를 당한 횟수
        long reportCount = reportRepository.countByReceiver(receiver);
        // 신고를 10번 당할 때마다 계정이 정지되게 한다
        if (reportCount > 0 && reportCount % LIMIT_REPORT_COUNT_STOPPED == 0) {
            // 신고를 당한 횟수가 총 30번을 초과하면 해당 계정은 회원 탈퇴되고, 블랙리스트에 등록된다
            if (reportCount >= LIMIT_REPORT_COUNT_BLACK) {
                receiver.setUnsubscribe();
                receiver.getProfile().setUnsubscribe();
                BlackList blackList = BlackList.builder()
                    .phone(receiver.getPhone())
                    .reason("신고 횟수 초과")
                    .createdAt(LocalDateTime.now())
                    .build();
                blackListRepository.save(blackList);
            } else {
                receiver.setStopped();
            }
            userRepository.save(receiver);
            profileRepository.save(receiver.getProfile());

            // 로그아웃되게 하기
            refreshTokenRepository.findById(receiver.getPhone())
                .ifPresent(refreshTokenRepository::delete);
        }
    }

    // 일정시간마다 Scheduling 작동.
    @Transactional
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul") // 1시간마다 작동
    public void deleteUnsubUser() {
        userRepository.deleteUnsubUser();
        userRepository.derestrictStoppedUser();
    }
}
