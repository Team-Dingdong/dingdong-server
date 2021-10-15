package dingdong.dingdong.domain.post;


import com.fasterxml.jackson.annotation.JsonBackReference;
import dingdong.dingdong.domain.BaseTimeEntity;
import dingdong.dingdong.domain.user.User;
import dingdong.dingdong.dto.post.PostCreateRequestDto;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id", nullable = false)
    private Long id;

    private String title;

    private int cost;

    private int people;

    @ColumnDefault("1")
    private int gatheredPeople;

    private String local;

    private String bio;

    @Column(columnDefinition = "varchar(255) default 'https://dingdongbucket.s3.ap-northeast-2.amazonaws.com/static/default_post.png'")
    private String imageUrl1;

    @Column(columnDefinition = "varchar(255) default 'https://dingdongbucket.s3.ap-northeast-2.amazonaws.com/static/default_post.png'")
    private String imageUrl2;

    @Column(columnDefinition = "varchar(255) default 'https://dingdongbucket.s3.ap-northeast-2.amazonaws.com/static/default_post.png'")
    private String imageUrl3;

    @Column(columnDefinition = "boolean default false")
    private boolean done;

    public void confirmed() {
        this.done = true;
    }

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonBackReference
    private Category category;

    public void setCategory(Category category) {
        this.category = category;

        if (!category.getPosts().contains(this)) {
            category.getPosts().add(this);
        }
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    public void setUser(User user) {
        this.user = user;

        if (!user.getPosts().contains(this)) {
            user.getPosts().add(this);
        }
    }

    public void setTitle(String title){ this.title = title; }
    public void setCost(int cost){ this.cost = cost; }
    public void setPeople(int people){ this.people = people; }
    public void setBio(String bio){ this.bio = bio; }
    public void setLocal(String local){ this.local = local; }

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonBackReference
    private List<PostTag> postTags = new ArrayList<>();


    public void plusUserCount() {
        this.gatheredPeople =
            this.gatheredPeople == this.people ? this.gatheredPeople : this.gatheredPeople + 1;
    }

    public void minusUserCount() {
        this.gatheredPeople = this.gatheredPeople == 0 ? 0 :this.gatheredPeople - 1;
    }
}
