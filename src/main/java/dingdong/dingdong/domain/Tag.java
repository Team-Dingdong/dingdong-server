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
public class Tag {

    @Id @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "TAG_ID", length = 10, nullable = false)
    private long tag_id;

    @Column(length = 10,nullable = false)
    private String tag_name;
}
