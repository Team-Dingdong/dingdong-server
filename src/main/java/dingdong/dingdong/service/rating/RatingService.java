package dingdong.dingdong.service.rating;

import dingdong.dingdong.domain.chat.ChatJoinRepository;
import dingdong.dingdong.domain.user.Profile;
import dingdong.dingdong.domain.user.ProfileRepository;
import dingdong.dingdong.domain.user.Rating;
import dingdong.dingdong.domain.user.RatingRepository;
import dingdong.dingdong.domain.user.User;
import dingdong.dingdong.domain.user.UserRepository;
import dingdong.dingdong.dto.rating.RatingRequestDto;
import dingdong.dingdong.dto.rating.RatingResponseDto;
import dingdong.dingdong.util.exception.ForbiddenException;
import dingdong.dingdong.util.exception.ResourceNotFoundException;
import dingdong.dingdong.util.exception.ResultCode;
import java.util.List;
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
    private final ChatJoinRepository chatJoinRepository;

    // 평가 조회
    @Transactional
    public RatingResponseDto getRating(Long id) {
        Profile profile = profileRepository.findByUserId(id)
            .orElseThrow(() -> new ResourceNotFoundException(ResultCode.PROFILE_NOT_FOUND));

        return RatingResponseDto.from(profile);
    }

    // 평가 생성
    @Transactional
    public void createRating(User sender, Long userId, RatingRequestDto ratingRequestDto) {
        User receiver = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(ResultCode.USER_NOT_FOUND));

        if (sender.getId() == receiver.getId()) {
            throw new ForbiddenException(ResultCode.RATING_CREATE_FAIL_SELF);
        }

        List<Long> users = chatJoinRepository.existsUserByUser(sender.getId());
        if (!users.contains(userId)) {
            throw new ForbiddenException(ResultCode.RATING_CREATE_FAIL_FORBIDDEN);
        }

        Rating rating = Rating.builder()
                .sender(sender)
                .receiver(receiver)
                .type(ratingRequestDto.getType())
                .build();

        ratingRepository.save(rating);

        Long goodCount = ratingRepository.countByReceiverAndType(receiver, RatingType.GOOD);
        Long badCount = ratingRepository.countByReceiverAndType(receiver, RatingType.BAD);

        Profile receiverProfile = profileRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException(ResultCode.PROFILE_NOT_FOUND));
        receiverProfile.setRating(goodCount, badCount);

        profileRepository.save(receiverProfile);
    }
}
