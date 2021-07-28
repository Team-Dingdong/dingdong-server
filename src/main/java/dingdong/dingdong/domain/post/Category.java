package dingdong.dingdong.domain.post;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Category {

    @Id
    @Column(name = "category_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;
}
