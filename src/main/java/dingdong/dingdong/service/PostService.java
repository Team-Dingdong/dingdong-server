package dingdong.dingdong.service;

import dingdong.dingdong.domain.post.Category;
import dingdong.dingdong.domain.post.CategoryRepository;
import dingdong.dingdong.domain.post.Post;
import dingdong.dingdong.domain.post.PostRepository;
import dingdong.dingdong.domain.user.*;
import dingdong.dingdong.dto.Post.PostCreationRequest;
import dingdong.dingdong.dto.Post.PostDetailResponse;
import dingdong.dingdong.dto.Post.PostGetResponse;
import dingdong.dingdong.dto.Post.PostUpdateRequest;
import dingdong.dingdong.util.exception.ForbiddenException;
import dingdong.dingdong.util.exception.ResourceNotFoundException;
import dingdong.dingdong.util.exception.Result;
import dingdong.dingdong.util.exception.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static dingdong.dingdong.util.exception.ResultCode.*;
import static java.time.LocalDateTime.now;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProfileRepository profileRepository;
    private final RatingRepository ratingRepository;

    // 홈화면 피드 GET
    public ResponseEntity<?> findPosts(Pageable pageable){
        Page<Post> postList = postRepository.findAll(pageable);

        Page<PostGetResponse> pagingList = postList.map(
                post -> new PostGetResponse(
                        post.getTitle(), post.getPeople(), post.getCost(),
                        post.getBio(), post.getImageUrl(), post.getLocal(),
                        post.getCreatedDate()
                ));

        return Result.toResult(ResultCode.POST_READ_SUCCESS, pagingList);
    }

    // 나누기 피드 상세보기
    public ResponseEntity<?> findPostById(Long id){

        Optional<Post> post = postRepository.findById(id);

        if(!post.isPresent()){
            return Result.toResult(ResultCode.POST_NOT_FOUND);
        }

        Optional<Profile> profile = profileRepository.findByUser_id(post.get().getUser().getId());
        if(!profile.isPresent()){
            return Result.toResult(ResultCode.PROFILE_NOT_FOUND);
        }

        Optional<Rating> rating = ratingRepository.findByUser_id(post.get().getUser().getId());
        if(!rating.isPresent()){
            return Result.toResult(ResultCode.RATING_NOT_FOUND);
        }

        PostDetailResponse postDetail = new PostDetailResponse(post.get().getTitle(), post.get().getCost(),
                post.get().getBio(), post.get().getImageUrl(), post.get().getCreatedDate(),
                post.get().getLocal(), profile.get().getNickname(),
                profile.get().getProfile_bio(), rating.get().getGood(), rating.get().getBad());

        return Result.toResult(ResultCode.POST_READ_SUCCESS, postDetail);
    }

    // 카테고리별로 나누기 피드 GET
    public ResponseEntity<?>  findPostByCategory_Id(Long id, Pageable pageable){
        Optional<Category> category = categoryRepository.findById(id);

        if(!category.isPresent()){
            return Result.toResult(ResultCode.CATEGORY_NOT_FOUND);
        }

        Page<Post> postList = postRepository.findByCategory_Id(category.get().getId(),  pageable);

        Page<PostGetResponse> pagingList = postList.map(
                post -> new PostGetResponse(
                        post.getTitle(), post.getPeople(), post.getCost(),
                        post.getBio(), post.getImageUrl(), post.getLocal(),
                        post.getCreatedDate()
                ));

        return Result.toResult(ResultCode.POST_READ_SUCCESS, pagingList);
    }


    // 나누기 피드(post) 생성
    public ResponseEntity<?> createPost(PostCreationRequest request){
        Post post = new Post();

        if(request == null) {
            return Result.toResult(ResultCode.POST_CREATE_FAIL);
        }

        // User_id
        Optional<User> user = userRepository.findById(request.getUser_id());
        if(!user.isPresent()){
            return Result.toResult(ResultCode.MEMBER_NOT_FOUND);
        }

        post.setUser(user.get());

        // Category_id
        Optional<Category> category = categoryRepository.findById(request.getCategory_id());
        if(!category.isPresent()){
            return Result.toResult(ResultCode.CATEGORY_NOT_FOUND);
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

            postRepository.save(post);
            return Result.toResult(ResultCode.POST_CREATE_SUCCESS);
    }
    
    // 나누기 피드(post) 제거
    public ResponseEntity<?>  deletePost(Long id){
        Optional<Post> post = postRepository.findById(id);
        if(!post.isPresent()) {
            return Result.toResult(ResultCode.POST_NOT_FOUND);
        }

        postRepository.deleteById(post.get().getId());
        return Result.toResult(ResultCode.POST_DELETE_SUCCESS);
    }

    // 나누기 피드(post) 수정
    public ResponseEntity<?> updatePost(Long id, PostUpdateRequest request){

        Optional<Post> optionalPost = postRepository.findById(id);
        if(!optionalPost.isPresent()){
            return Result.toResult(ResultCode.POST_NOT_FOUND);
        }
        Post post = optionalPost.get();

        // Category_id
        Optional<Category> category = categoryRepository.findById(request.getCategory_id());
        if(!category.isPresent()){
            return Result.toResult(ResultCode.CATEGORY_NOT_FOUND);
        }
        post.setCategory(category.get());

        post.setTitle(request.getTitle());
        post.setPeople(request.getPeople());
        post.setCost(request.getCost());
        post.setBio(request.getBio());
        post.setLocal(request.getLocal());
        post.setImageUrl(request.getImageUrl());

        postRepository.save(post);
        return Result.toResult(ResultCode.POST_UPDATE_SUCCESS);
    }
}
