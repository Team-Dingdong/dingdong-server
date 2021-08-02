package dingdong.dingdong.domain.user;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Local {

    @Id
    @Column(name = "local_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

}
