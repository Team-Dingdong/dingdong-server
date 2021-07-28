package dingdong.dingdong.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;


@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Post_Tag {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(length = 10,nullable = false)
    private long post_tag_id;

    @OneToOne
    @JoinColumn(name = "POST_ID")
    private long post_id;

    @OneToOne
    @JoinColumn(name = "TAG_ID")
    private long tag_id;

    @Column(length = 10,nullable = false)
    private long order_num;
}
