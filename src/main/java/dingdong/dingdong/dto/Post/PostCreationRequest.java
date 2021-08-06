package dingdong.dingdong.dto.Post;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostCreationRequest {
    private String title;
    private int people;
    private int cost;
    private String bio;
    private String local;
    private String imageUrl;
    //private LocalDateTime modifiedDate;
    private long category_id;
    private long user_id;
}
