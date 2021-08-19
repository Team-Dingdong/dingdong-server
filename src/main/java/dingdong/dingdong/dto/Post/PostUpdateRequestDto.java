package dingdong.dingdong.dto.Post;

import lombok.Data;

@Data
public class PostUpdateRequestDto {

    private String title;
    private int people;
    private int cost;
    private String bio;
    private String imageUrl;
    private String local;
    private Long categoryId;
}
