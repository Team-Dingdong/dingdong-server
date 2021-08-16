package dingdong.dingdong.dto.post;

import lombok.Data;

@Data
public class PostUpdateRequest {

    private String title;
    private int people;
    private int cost;
    private String bio;
    private String imageUrl;
    private String local;
    private long categoryId;
}