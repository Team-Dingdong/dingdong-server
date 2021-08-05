package dingdong.dingdong.dto.Post;

import javax.validation.constraints.NotNull;

import dingdong.dingdong.domain.post.Post;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
@Getter
public class PostDto {

    //private long user_id;

    @NotNull
    private String title;

    @NotNull
    private Number people;

    @NotNull
    private int cost;

    @NotNull
    private String bio;

    private String imageUrl;

    @NotNull
    private LocalDateTime postDate;

    //@NotNull
    //private long category_id;
    //private long post_tags_id;

    public PostDto(Post entity){
        this.title = entity.getTitle();
        this.people = entity.getPeople();
        this.cost = entity.getCost();
        this.bio = entity.getBio();
        this.imageUrl = entity.getImageUrl();
        //this.postDate = entity.getPostDate();
        //this.category_id = entity.getCategory();
        //this.user_id = user_id;

    }
}
