package dingdong.dingdong.dto.Post;

import lombok.Data;

@Data
public class PostUpdateRequest {

    private String title;
    private Number people;
    private int cost;
    private String bio;
    private String imageUrl;
    private long category_id;
}
