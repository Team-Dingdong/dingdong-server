package dingdong.dingdong.domain.user;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rating {

    @Id
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @ColumnDefault("0")
    private int good;

    @ColumnDefault("0")
    private int bad;

    @ColumnDefault("0")
    private int total;

}
