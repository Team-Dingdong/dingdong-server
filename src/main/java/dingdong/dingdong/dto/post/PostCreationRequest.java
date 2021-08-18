package dingdong.dingdong.dto.post;

import lombok.Data;

@Data
public class PostCreationRequest {
    private String title;
    private int people;
    private int cost;
    private String bio;
    private String local;
    private String imageUrl;
    private long category_id;
    private long user_id;
}
