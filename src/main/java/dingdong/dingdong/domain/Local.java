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
@Table(name = "Local")
public class Local {

    @Id @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "LOCAL_ID")
    private long local_id;

    @Column(length = 30, nullable = false)
    private String local_name;
}
