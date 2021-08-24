package dingdong.dingdong.dto.post;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
public class PostGetResponseDto {

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

    public PostGetResponseDto(String title, int people, int cost, String bio, String imageUrl1,
                              String local, LocalDateTime createdDate){
        this.title = title;
        this.people = people;
        this.cost = cost;
        this.bio = bio;
        this.local = local;
        this.imageUrl1 = imageUrl1;
        this.createdDate = createdDate;
    }
}
