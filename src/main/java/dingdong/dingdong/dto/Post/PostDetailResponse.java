package dingdong.dingdong.dto.Post;

import javax.validation.constraints.NotNull;

import dingdong.dingdong.domain.post.Category;
import dingdong.dingdong.domain.post.Post;
import dingdong.dingdong.domain.user.Profile;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
@Getter
public class PostDetailResponse {

    @NotNull
    private String title;

    @NotNull
    private String nickname;

    @NotNull
    private String profile_bio;

    @NotNull
    private Number good;

    @NotNull
    private Number bad;

    //private String tag_name;

    @NotNull
    private int cost;

    @NotNull
    private String bio;

    @NotNull
    private String local;

    private LocalDateTime createdDate;

    private String imageUrl;

    public PostDetailResponse(String title, int cost, String bio, String imageUrl, LocalDateTime createdDate,
                              String local, String nickname, String profile_bio, int good, int bad){
        this.title = title;
        this.cost = cost;
        this.bio = bio;
        this.imageUrl = imageUrl;
        this.createdDate = createdDate;
        this.local = local;
        this.nickname = nickname;
        this.profile_bio = profile_bio;
        this.good = good;
        this.bad = bad;

    }
}
