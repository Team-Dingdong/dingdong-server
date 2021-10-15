package dingdong.dingdong.dto.rating;

import dingdong.dingdong.service.rating.RatingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingRequestDto {

    private RatingType type;
}
