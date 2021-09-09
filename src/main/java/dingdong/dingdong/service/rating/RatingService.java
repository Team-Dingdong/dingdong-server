package dingdong.dingdong.service.rating;

import dingdong.dingdong.domain.user.*;
import dingdong.dingdong.dto.rating.RatingRequestDto;
import dingdong.dingdong.dto.rating.RatingResponseDto;
import dingdong.dingdong.util.exception.ForbiddenException;
import dingdong.dingdong.util.exception.ResourceNotFoundException;
import dingdong.dingdong.util.exception.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class RatingService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;

    // 평가 조회
    @Transactional
    public RatingResponseDto getRating(Long id) {
        Profile profile = profileRepository.findByUserId(id).orElseThrow(() -> new ResourceNotFoundException(ResultCode.PROFILE_NOT_FOUND));

        return RatingResponseDto.from(profile);
    }

    // 평가 생성
    @Transactional
    public void createRating(User sender, Long userId, RatingRequestDto ratingRequestDto) {
        User receiver = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(ResultCode.USER_NOT_FOUND));

        if(sender.getId() == receiver.getId()) {
            throw new ForbiddenException(ResultCode.RATING_CREATE_FAIL_SELF);
        }

        Rating rating = ratingRepository.findBySenderAndReceiver(sender, receiver).orElse(new Rating(sender, receiver));
        rating.setType(ratingRequestDto.getType());
        ratingRepository.save(rating);

        Long goodCount = ratingRepository.countByReceiverAndType(receiver, RatingType.GOOD);
        Long badCount = ratingRepository.countByReceiverAndType(receiver, RatingType.BAD);

        Profile receiverProfile = profileRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException(ResultCode.PROFILE_NOT_FOUND));
        receiverProfile.setRating(goodCount, badCount);

        profileRepository.save(receiverProfile);
    }
}
