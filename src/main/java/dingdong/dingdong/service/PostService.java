package dingdong.dingdong.service;

import dingdong.dingdong.domain.post.Category;
import dingdong.dingdong.domain.post.CategoryRepository;
import dingdong.dingdong.domain.post.Post;
import dingdong.dingdong.domain.post.PostRepository;
import dingdong.dingdong.domain.user.User;
import dingdong.dingdong.domain.user.UserRepository;
import dingdong.dingdong.dto.Post.PostCreationRequest;
import dingdong.dingdong.dto.Post.PostDto;
import dingdong.dingdong.util.exception.DuplicateException;
import dingdong.dingdong.util.exception.ForbiddenException;
import dingdong.dingdong.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static dingdong.dingdong.util.exception.ResultCode.*;
import static java.time.LocalDateTime.now;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public Page<Post> findPosts(Pageable pageable){
        Page<Post> list = postRepository.findAll(pageable);
        return list;
    }

    public PostDto findPostById(Long id){
        Post entity = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND));

        return new PostDto(entity);
    }


    // post 생성
    //@Transactional
    public Post createPost(PostCreationRequest request){
        Post post = new Post();

        // User_id
        Optional<User> user = userRepository.findById(request.getUser_id());
        if(!user.isPresent()){
            throw new ResourceNotFoundException(MEMBER_NOT_FOUND);
        }

        post.setUser(user.get());

        // Category_id
        Optional<Category> category = categoryRepository.findById(request.getCategory_id());
        if(!category.isPresent()){
            throw new ResourceNotFoundException(CATEGORY_NOT_FOUND);
        }
        post.setCategory(category.get());

        // title, people, price, bio, imageUrl
        post.setTitle(request.getTitle());
        post.setPeople(request.getPeople());
        post.setCost(request.getCost());
        post.setBio(request.getBio());
        post.setLocal(request.getLocal());
        post.setImageUrl(request.getImageUrl());
        post.setDone(false);

        if (post == null){
            throw new ForbiddenException(POST_CREATE_FAIL);
        }
        return postRepository.save(post);

    }
    
    // post 제거
    public void deletePost(Long id){
        postRepository.delete(
                postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND))
        );
    }

    // post 수정
    public Post updatePost(Long id, PostCreationRequest request){
        Optional<Post> optionalPost = postRepository.findById(id);
        if(!optionalPost.isPresent()){
            throw new ResourceNotFoundException(POST_NOT_FOUND);
        }
        Post post = optionalPost.get();

        // Category_id
        Optional<Category> category = categoryRepository.findById(request.getCategory_id());
        if(!category.isPresent()){
            throw new ResourceNotFoundException(CATEGORY_NOT_FOUND);
        }
        post.setCategory(category.get());

        post.setTitle(request.getTitle());
        post.setPeople(request.getPeople());
        post.setCost(request.getCost());
        post.setBio(request.getBio());
        post.setLocal(request.getLocal());
        post.setImageUrl(request.getImageUrl());

        return postRepository.save(post);
    }
}
