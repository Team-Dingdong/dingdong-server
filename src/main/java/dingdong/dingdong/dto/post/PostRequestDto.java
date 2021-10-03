package dingdong.dingdong.dto.post;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostRequestDto {
    private String title;
    private int people;
    private int cost;
    private String bio;
    private String local;
    private String postTag;
    private Long categoryId;

    public PostRequestDto(String title,int people,int cost,String bio,String local,String postTag, Long categoryId){
        this.title = title;
        this.people = people;
        this.cost = cost;
        this.bio = bio;
        this.local = local;
        this.postTag = postTag;
        this.categoryId = categoryId;
    }
}
