package dingdong.dingdong.dto.Post;

import javax.validation.constraints.NotNull;

import dingdong.dingdong.domain.post.Category;
import dingdong.dingdong.domain.post.Post;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
@Getter
public class PostGetResponse {

    @NotNull
    private String title;

    @NotNull
    private int people;

    @NotNull
    private int cost;

    @NotNull
    private String bio;

    @NotNull
    private String local;

    private LocalDateTime createdDate;

    private String imageUrl;

    public PostGetResponse(String title, int people, int cost, String bio, String imageUrl,
                           String local, LocalDateTime createdDate){
        this.title = title;
        this.people = people;
        this.cost = cost;
        this.bio = bio;
        this.local = local;
        this.imageUrl = imageUrl;
        this.createdDate = createdDate;
    }
}
