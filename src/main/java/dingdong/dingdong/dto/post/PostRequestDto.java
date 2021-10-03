package dingdong.dingdong.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostRequestDto {

    private String title;

    private int people;

    private int cost;

    private String bio;

    private String local;

    private String postTag;

    private Long categoryId;

}
