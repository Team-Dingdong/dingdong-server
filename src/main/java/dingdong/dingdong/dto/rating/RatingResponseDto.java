package dingdong.dingdong.dto.rating;

import dingdong.dingdong.domain.user.Profile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
