package dingdong.dingdong.domain.user;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import dingdong.dingdong.domain.BaseTimeEntity;
import dingdong.dingdong.domain.post.Post;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Column(unique = true)
    private String phone;

    private LocalDateTime localDate;

    private LocalDateTime deletedDate;

    private Role authority;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "local1")
    private Local local1;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "local2")
    private Local local2;

    @OneToOne(mappedBy = "user", fetch = FetchType.EAGER)
    private Profile profile;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Post> posts = new ArrayList<>();

    public void setLocal(Local local1, Local local2) {
        this.local1 = local1;
        this.local2 = local2;
        this.localDate = LocalDateTime.now();
    }

    public void setUnsubscribe() {
        this.authority = Role.UNSUB;
        this.deletedDate = LocalDateTime.now();
    }

    public void setStopped() {
        this.authority = Role.STOPPED;
    }
}
