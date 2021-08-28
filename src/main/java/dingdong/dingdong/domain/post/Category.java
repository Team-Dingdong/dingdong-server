package dingdong.dingdong.domain.post;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @Column(name = "category_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Post> posts = new ArrayList<>();

    public void addPost(Post post){
        this.posts.add(post);

        if(post.getCategory() != this){
            post.setCategory(this);
        }
    }

    public Category(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
