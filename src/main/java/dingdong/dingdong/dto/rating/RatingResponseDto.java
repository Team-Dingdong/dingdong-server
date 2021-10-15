package dingdong.dingdong.dto.rating;

import dingdong.dingdong.domain.user.Profile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingResponseDto {

    private Long good;

    private Long bad;

    private Long total;

    public static RatingResponseDto from(Profile profile) {
        return RatingResponseDto.builder()
            .good(profile.getGood())
            .bad(profile.getBad())
            .total(profile.getGood() + profile.getBad())
            .build();
    }
}
