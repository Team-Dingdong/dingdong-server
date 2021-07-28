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
@Table(name = "Report")
public class Report {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(length = 10,nullable = false)
    private long report_id;

    @Column(length = 20,nullable = false)
    private String reason;

    @Column(length = 10,nullable = false)
    private long sender;

    @Column(length = 10,nullable = false)
    private long receiver;

    private LocalDateTime report_date;

    @PrePersist // DB에 insert 되기 직전에 실행
    public void report_date() {
        this.report_date =  LocalDateTime.now();
    }
}
