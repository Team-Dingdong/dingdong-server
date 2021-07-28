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
@Table(name = "Rating")
public class Rating {

    @OneToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @Column(length = 10,nullable = false)
    private int good;
    @Column(length = 10,nullable = false)
    private int bad;

    @Column(length = 10,nullable = false)
    private int total;
}
