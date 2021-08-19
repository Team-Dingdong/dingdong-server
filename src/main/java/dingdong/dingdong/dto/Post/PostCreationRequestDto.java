package dingdong.dingdong.dto.Post;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostCreationRequestDto {
    private String title;
    private int people;
    private int cost;
    private String bio;
    private String local;
    private Long categoryId;
}
