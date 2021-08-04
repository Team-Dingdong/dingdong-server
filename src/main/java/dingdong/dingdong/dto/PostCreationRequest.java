package dingdong.dingdong.dto;

import lombok.Data;

@Data
public class PostCreationRequest {

    private String title;
    private int people;
    private int cost;
    private String bio;
    private String imageUrl;
    private long category_id;
    private long user_id;
    //private long post_tags_id;

    // private String local;
}
