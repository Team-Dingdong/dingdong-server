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
public class Post {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "POST_ID", length = 10, nullable = false)
    private long post_id;

    @Column(length = 20,nullable = false)
    private String title;

    @Column(length = 20,nullable = false)
    private String cost;

    @Column(length = 20,nullable = false)
    private String Image_url;

    @OneToOne
    @JoinColumn(name = "CATEGORY_ID")
    private String category_id;

    @Column(length = 20,nullable = false)
    private String product;

    @Column(length = 20,nullable = false)
    private String bio;

    @Column(length = 20,nullable = false)
    private int number_people;

    @OneToOne
    @JoinColumn(name = "USER_ID")
    private long written_id;

    @Column(length = 10,nullable = false)
    private String state; // 종료 여부 (Y, N)

    private LocalDateTime create_date;

    @PrePersist // DB에 insert 되기 직전에 실행
    public void create_date() {
        this.create_date =  LocalDateTime.now();
    }

    private LocalDateTime end_date; // 형태 TBD

}
