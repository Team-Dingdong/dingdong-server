package dingdong.dingdong.service;

import dingdong.dingdong.domain.post.Category;
import dingdong.dingdong.domain.post.CategoryRepository;
import dingdong.dingdong.domain.post.Post;
import dingdong.dingdong.domain.post.PostRepository;
import dingdong.dingdong.domain.user.User;
import dingdong.dingdong.domain.user.UserRepository;
import dingdong.dingdong.dto.Post.PostCreationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static java.time.LocalDateTime.now;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public Post loadPostById(Long id){
        Optional<Post> post = postRepository.findById(id);
        if(post.isPresent()){
            return post.get();
        }
        throw new EntityNotFoundException("Cant find any post under given id");
    }

    public List<Post> loadPosts(){
        return postRepository.findAll();
    }

    // post 생성
    //@Transactional
    public Post createPost(PostCreationRequest request){
        Post post = new Post();

        // User_id
        Optional<User> user = userRepository.findById(request.getUser_id());
        if(!user.isPresent()){
            throw new NoSuchElementException("No value present");
        }
        post.setUser(user.get());

        // Category_id
        Optional<Category> category = categoryRepository.findById(request.getCategory_id());
        if(!category.isPresent()){
            throw new NoSuchElementException("No value present");
        }
        post.setCategory(category.get());

        // title, people, price, bio, imageUrl
        post.setTitle(request.getTitle());
        post.setPeople(request.getPeople());
        post.setCost(request.getCost());
        post.setBio(request.getBio());
        post.setImageUrl(request.getImageUrl());
        post.setDone(false);
        //post.setPostDate(now());
        //post.setPostTags();

        return postRepository.save(post);

    }
    
    // post 제거
    public void deletePost(Long id){
        Optional<Post> post = postRepository.findById(id);

        postRepository.deleteById(id);
    }

    // post 수정
    public Post updatePost(Long id, PostCreationRequest request){
        Optional<Post> optionalPost = postRepository.findById(id);

        if(optionalPost.isEmpty()){
            throw new EntityNotFoundException("Post not present in the database");
        }

        Post post = optionalPost.get();

        // Category_id
        Optional<Category> category = categoryRepository.findById(request.getCategory_id());
        if(!category.isPresent()){

        }
        post.setCategory(category.get());

        post.setTitle(request.getTitle());
        post.setPeople(request.getPeople());
        post.setCost(request.getCost());
        post.setBio(request.getBio());
        post.setImageUrl(request.getImageUrl());
        //post.setPostTags(request.getPost_tags_id());

        return postRepository.save(post);
    }
}
