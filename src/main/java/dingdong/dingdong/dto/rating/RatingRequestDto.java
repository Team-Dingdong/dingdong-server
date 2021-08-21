package dingdong.dingdong.dto.rating;

import dingdong.dingdong.service.rating.RatingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RatingRequestDto {

    private RatingType type;
}
