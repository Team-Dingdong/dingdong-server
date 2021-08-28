package dingdong.dingdong.dto.post;

import javax.validation.constraints.NotNull;

import dingdong.dingdong.domain.post.Post;
import dingdong.dingdong.domain.user.Profile;
import dingdong.dingdong.dto.profile.ProfileResponseDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostGetResponseDto {
    
    @NotNull
    private Long id;

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

    private String imageUrl1;

    private String tag;

    public static PostGetResponseDto from(Post post) {
        return PostGetResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .people(post.getPeople())
                .cost(post.getCost())
                .bio(post.getBio())
                .local(post.getLocal())
                .imageUrl1(post.getImageUrl1())
                .createdDate(post.getCreatedDate())
                .build();
    }
}
