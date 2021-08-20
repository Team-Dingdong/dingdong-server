package dingdong.dingdong.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rating {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rating_id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from")
    private User from;

    @Id
    @ManyToOne
    @JoinColumn(name = "to")
    private User to;

}
