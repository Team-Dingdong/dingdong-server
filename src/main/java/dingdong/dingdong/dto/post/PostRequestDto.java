package dingdong.dingdong.dto.post;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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
