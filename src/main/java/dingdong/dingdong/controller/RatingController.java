package dingdong.dingdong.controller;

import dingdong.dingdong.domain.user.CurrentUser;
import dingdong.dingdong.domain.user.User;
import dingdong.dingdong.dto.rating.RatingRequestDto;
import dingdong.dingdong.dto.rating.RatingResponseDto;
import dingdong.dingdong.service.rating.RatingService;
import dingdong.dingdong.service.rating.RatingType;
import dingdong.dingdong.util.exception.Result;
import dingdong.dingdong.util.exception.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/rating")
public class RatingController {

    private final RatingService ratingService;

    // 평가 조회
    @GetMapping("")
    public ResponseEntity<Result<RatingResponseDto>> getRating(@CurrentUser User user) {
        RatingResponseDto data = ratingService.getRating(user);
        return Result.toResult(ResultCode.RATING_READ_SUCCESS, data);
    }

    // 평가 생성
    @PostMapping("/{userId}")
    public ResponseEntity<Result> createRating(@CurrentUser User user, @PathVariable Long userId, @RequestBody RatingRequestDto ratingRequestDto) {
        ratingService.createRating(user, userId, ratingRequestDto);
        return Result.toResult(ResultCode.RATING_CREATE_SUCCESS);
    }

    // 신고 하기
}