package dingdong.dingdong.dto.post;

import javax.validation.constraints.NotNull;
import dingdong.dingdong.domain.post.Category;
import dingdong.dingdong.domain.post.Post;
import dingdong.dingdong.domain.user.Profile;
import dingdong.dingdong.domain.user.Rating;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Optional;

@Data
@Getter
public class PostDetailResponseDto {

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

    @NotNull
    private int cost;

    @NotNull
    private String bio;

    @NotNull
    private String local;

    @NotNull
    private LocalDateTime createdDate;

    @NotNull
    private LocalDateTime modifiedDate;

    @NotNull
    private int people;

    @NotNull
    private int gatheredPeople;

    private String imageUrl;

    public PostDetailResponseDto(Post post, Profile profile, Rating rating){
        this.title = post.getTitle();
        this.cost = post.getCost();
        this.bio = post.getBio();
        this.imageUrl = post.getImageUrl();
        this.createdDate = post.getCreatedDate();
        this.modifiedDate = post.getModifiedDate();
        this.people = post.getPeople();
        this.gatheredPeople = post.getGatheredPeople();
        this.local = post.getLocal();
        this.nickname = profile.getNickname();
        this.profile_bio = profile.getProfile_bio();
        this.good = rating.getGood();
        this.bad = rating.getBad();
    }
}
