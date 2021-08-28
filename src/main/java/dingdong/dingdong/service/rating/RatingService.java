package dingdong.dingdong.service.rating;

import dingdong.dingdong.domain.user.*;
import dingdong.dingdong.dto.rating.RatingRequestDto;
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
    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;

    // 평가 조회
    public RatingResponseDto getRating(Long id) {
        Profile profile = profileRepository.findByUserId(id).orElseThrow(() -> new ResourceNotFoundException(ResultCode.PROFILE_NOT_FOUND));
        log.info("profile : {}", profile);
        return RatingResponseDto.from(profile);
    }

    // 평가 생성
    public void createRating(User sender, Long userId, RatingRequestDto ratingRequestDto) {
        User receiver = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(ResultCode.USER_NOT_FOUND));

        Rating rating = ratingRepository.findBySenderAndReceiver(sender, receiver).orElse(new Rating(sender, receiver));
        rating.setType(ratingRequestDto.getType());

        ratingRepository.save(rating);

        Long goodCount = ratingRepository.countByReceiverAndType(receiver, RatingType.GOOD);
        Long badCount = ratingRepository.countByReceiverAndType(receiver, RatingType.BAD);
        log.info("goodCount : {}, badCount : {}", goodCount, badCount);

        Profile receiverProfile = profileRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException(ResultCode.PROFILE_NOT_FOUND));
        receiverProfile.setRating(goodCount, badCount);

        profileRepository.save(receiverProfile);
    }

}
