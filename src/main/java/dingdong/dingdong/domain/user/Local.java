package dingdong.dingdong.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Local {

    @Id
    @Column(name = "local_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

}
