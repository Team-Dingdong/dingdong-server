package dingdong.dingdong.service.rating;

import dingdong.dingdong.domain.user.*;
import dingdong.dingdong.dto.rating.RatingResponseDto;
import dingdong.dingdong.util.exception.ResourceNotFoundException;
import dingdong.dingdong.util.exception.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RatingService {

    private final ProfileRepository profileRepository;
    private final RatingRepository ratingRepository;

    // 평가 조회
    public RatingResponseDto getRating(User user) {
        Profile profile = profileRepository.findById(user.getId()).orElseThrow(() -> new ResourceNotFoundException(ResultCode.PROFILE_NOT_FOUND));
        return RatingResponseDto.from(profile);
    }

    // 평가 생성
    public void createRating(User user, Long userId, RatingType type) {
        Rating rating = ratingRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException(ResultCode.RATING_NOT_FOUND));
    }

}
