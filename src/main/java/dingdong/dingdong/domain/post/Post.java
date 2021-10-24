package dingdong.dingdong.domain.post;


import com.fasterxml.jackson.annotation.JsonBackReference;
import dingdong.dingdong.domain.BaseTimeEntity;
import dingdong.dingdong.domain.user.Local;
import dingdong.dingdong.domain.user.User;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
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

    private String location;

    private String bio;

    @Column(columnDefinition = "varchar(255) default 'https://dingdongbucket.s3.ap-northeast-2.amazonaws.com/static/default_post.png'")
    private String imageUrl1;

    @Column(columnDefinition = "varchar(255) default 'https://dingdongbucket.s3.ap-northeast-2.amazonaws.com/static/default_post.png'")
    private String imageUrl2;

    @Column(columnDefinition = "varchar(255) default 'https://dingdongbucket.s3.ap-northeast-2.amazonaws.com/static/default_post.png'")
    private String imageUrl3;

    @Column(columnDefinition = "Boolean default false")
    private Boolean done;

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

    @ManyToOne
    @JoinColumn(name = "local_id")
    private Local local;

    public void setTitle(String title){ this.title = title; }
    public void setCost(int cost){ this.cost = cost; }
    public void setPeople(int people){ this.people = people; }
    public void setBio(String bio){ this.bio = bio; }
    public void setLocal(String location){ this.location = location; }
    public void setImageUrl(String imageUrl1, String imageUrl2, String imageUrl3){
        this.imageUrl1 = imageUrl1;
        this.imageUrl2 = imageUrl2;
        this.imageUrl3 = imageUrl3;
    }

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonBackReference
    @Builder.Default
    private List<PostTag> postTags = new ArrayList<>();

    public void plusUserCount() {
        this.gatheredPeople =
            this.gatheredPeople == this.people ? this.gatheredPeople : this.gatheredPeople + 1;
    }

    public void minusUserCount() {
        this.gatheredPeople = this.gatheredPeople == 0 ? 0 :this.gatheredPeople - 1;
    }
}
