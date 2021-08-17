package dingdong.dingdong.dto.Post;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.hibernate.annotations.ColumnDefault;

@Data
@NoArgsConstructor
public class PostCreationRequestDto {
    private String title;
    private int people;
    private int cost;
    private String bio;
    private String local;
    private long category_id;
    private long user_id;
}
