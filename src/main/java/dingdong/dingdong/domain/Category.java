package dingdong.dingdong.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;


@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Category {

    @Id @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(length = 10,nullable = false, name = "CATEGORY_ID")
    private long category_id;

    @Column(length = 10,nullable = false)
    private String category_name;
}
