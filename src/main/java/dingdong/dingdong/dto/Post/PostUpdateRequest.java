package dingdong.dingdong.dto.Post;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostUpdateRequest {

    private String title;
    private int people;
    private int cost;
    private String bio;
    private String imageUrl;
    private String local;
    private long category_id;
}
