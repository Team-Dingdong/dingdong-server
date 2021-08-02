package dingdong.dingdong.domain.post;

import dingdong.dingdong.domain.user.User;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Post {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Number cost;


    private Number people;

    private String bio;

    private String imageUrl;

    @Column(columnDefinition = "boolean default false")
    private boolean done;

    @CreatedDate
    private LocalDateTime postDate;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    public void setCategory(Category category) {
        this.category = category;
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public void setUser(User user) {
        this.user = user;

        if(!user.getPosts().contains(this)) {
            user.getPosts().add(this);
        }
    }

    @OneToMany(mappedBy = "post")
    private List<PostTag> tags = new ArrayList<>();
}