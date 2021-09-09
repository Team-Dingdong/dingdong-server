package dingdong.dingdong.dto.post;

import dingdong.dingdong.domain.post.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostGetResponseDto {

    private Long id;

    private String title;

    private int people;

    private int cost;

    private String bio;

    private String local;

    private boolean done;

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
                .done(post.isDone())
                .imageUrl1(post.getImageUrl1())
                .createdDate(post.getCreatedDate())
                .build();
    }
}
