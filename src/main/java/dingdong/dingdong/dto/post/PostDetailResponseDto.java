package dingdong.dingdong.dto.post;

import dingdong.dingdong.domain.post.Post;
import dingdong.dingdong.domain.user.Profile;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
public class PostDetailResponseDto {

    private String category;

    private Long userId;

    private String nickname;

    private String profileImageUrl;

    private String title;

    private Number good;

    private Number bad;

    private int cost;

    private String bio;

    private String local;

    private LocalDateTime createdDate;

    private LocalDateTime modifiedDate;

    private int people;

    private int gatheredPeople;

    private boolean done;

    private String imageUrl1;

    private String imageUrl2;

    private String imageUrl3;

    private List<String> tagList;

    public PostDetailResponseDto(Post post, Profile profile, List<String> tagList){
        
        this.category = post.getCategory().getName();
        this.title = post.getTitle();
        this.cost = post.getCost();
        this.bio = post.getBio();
        this.imageUrl1 = post.getImageUrl1();
        this.imageUrl2 = post.getImageUrl2();
        this.imageUrl3 = post.getImageUrl3();
        this.createdDate = post.getCreatedDate();
        this.modifiedDate = post.getModifiedDate();
        this.people = post.getPeople();
        this.gatheredPeople = post.getGatheredPeople();
        this.done = post.isDone();
        this.local = post.getLocal();
        this.userId = post.getUser().getId();
        this.nickname = profile.getNickname();
        this.profileImageUrl = profile.getProfileImageUrl();
        this.good = profile.getGood();
        this.bad = profile.getBad();
        this.tagList = tagList;
    }
}
