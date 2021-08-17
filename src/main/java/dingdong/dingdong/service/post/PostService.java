package dingdong.dingdong.service.post;

import dingdong.dingdong.domain.post.Category;
import dingdong.dingdong.domain.post.CategoryRepository;
import dingdong.dingdong.domain.post.Post;
import dingdong.dingdong.domain.post.PostRepository;
import dingdong.dingdong.domain.user.*;
import dingdong.dingdong.dto.Post.PostCreationRequestDto;
import dingdong.dingdong.dto.Post.PostDetailResponseDto;
import dingdong.dingdong.dto.Post.PostGetResponseDto;
import dingdong.dingdong.dto.Post.PostUpdateRequestDto;
import dingdong.dingdong.util.exception.ForbiddenException;
import dingdong.dingdong.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static dingdong.dingdong.util.exception.ResultCode.*;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProfileRepository profileRepository;
    private final RatingRepository ratingRepository;

    // 홈화면 피드 GET
    public Page<PostGetResponseDto> findPosts(Long local1, Long local2, Pageable pageable){
        Page<Post> postList = postRepository.findAll(local1, local2, pageable);

        Page<PostGetResponseDto> pagingList = postList.map(
                post -> new PostGetResponseDto(
                        post.getTitle(), post.getPeople(), post.getCost(),
                        post.getBio(), post.getImageUrl(), post.getLocal(),
                        post.getCreatedDate()
                ));

        return pagingList;
    }

    // 나누기 피드 상세보기
    public PostDetailResponseDto findPostById(Long id){

        Optional<Post> post = postRepository.findById(id);

        if(!post.isPresent()){
            throw new ResourceNotFoundException(POST_NOT_FOUND);
        }

        Optional<Profile> profile = profileRepository.findByUserId(post.get().getUser().getId());
        if(!profile.isPresent()){
            throw new ResourceNotFoundException(PROFILE_NOT_FOUND);
        }

        Optional<Rating> rating = ratingRepository.findByUser_id(post.get().getUser().getId());
        if(!rating.isPresent()){
            throw new ResourceNotFoundException(RATING_NOT_FOUND);
        }

        PostDetailResponseDto postDetail = new PostDetailResponseDto(post.get().getTitle(), post.get().getCost(),
                post.get().getBio(), post.get().getImageUrl(), post.get().getCreatedDate(),post.get().getModifiedDate(),
                post.get().getPeople(), post.get().getGatheredPeople(), post.get().getLocal(), profile.get().getNickname(),
                profile.get().getProfile_bio(), rating.get().getGood(), rating.get().getBad());

        return postDetail;
    }

    // 카테고리별로 나누기 피드 GET
    public Page<PostGetResponseDto>  findPostByCategory_Id(Long local1, Long local2, Long id, Pageable pageable){
        Optional<Category> category = categoryRepository.findById(id);

        if(!category.isPresent()){
            throw new ResourceNotFoundException(CATEGORY_NOT_FOUND);
        }

        Page<Post> postList = postRepository.findByCategory_Id(local1, local2, category.get().getId(),  pageable);

        Page<PostGetResponseDto> pagingList = postList.map(
                post -> new PostGetResponseDto(
                        post.getTitle(), post.getPeople(), post.getCost(),
                        post.getBio(), post.getImageUrl(), post.getLocal(),
                        post.getCreatedDate()
                ));

        return pagingList;
    }

    // 유저의 판매내역 리스트 (GET: 유저별로 출력되는 나누기 피드)
    public Page<PostGetResponseDto>  findPostByUser_Id(Long id, Pageable pageable){
        Optional<User> user = userRepository.findById(id);
        //Optional<User> user = userRepository.findByUser_id(id);

        if(!user.isPresent()){
            throw new ResourceNotFoundException(USER_NOT_FOUND);
        }

        Page<Post> postList = postRepository.findByUser_Id(user.get().getId(), pageable);

        Page<PostGetResponseDto> pagingList = postList.map(
                post -> new PostGetResponseDto(
                        post.getTitle(), post.getPeople(), post.getCost(),
                        post.getBio(), post.getImageUrl(), post.getLocal(),
                        post.getCreatedDate()
                ));

        return pagingList;
    }


    // 나누기 피드(post) 생성
    public void createPost(PostCreationRequestDto request) {
        Post post = new Post();

        if(request == null) {
            throw new ForbiddenException(POST_CREATE_FAIL);
        }

        // Category_id
        Optional<Category> category = categoryRepository.findById(request.getCategory_id());
        if(!category.isPresent()){
            throw new ResourceNotFoundException(CATEGORY_NOT_FOUND);
        }
        post.setCategory(category.get());

        // User_id
        Optional<User> user = userRepository.findById(request.getUser_id());
        if(!user.isPresent()){
            throw new ResourceNotFoundException(USER_NOT_FOUND);
        }
        post.setUser(user.get());

            // title, people, price, bio, imageUrl
            post.setTitle(request.getTitle());
            post.setPeople(request.getPeople());
            post.setCost(request.getCost());
            post.setBio(request.getBio());
            post.setLocal(request.getLocal());
            post.setDone(false);

            postRepository.save(post);
    }
    
    // 나누기 피드(post) 제거
    public void  deletePost(Long id){
        Optional<Post> post = postRepository.findById(id);
        if(!post.isPresent()) {
            throw new ResourceNotFoundException(POST_NOT_FOUND);
        }

        postRepository.deleteById(post.get().getId());
    }

    // 나누기 피드(post) 수정
    public void updatePost(Long id, PostUpdateRequestDto request){

        Optional<Post> optionalPost = postRepository.findById(id);
        if(!optionalPost.isPresent()){
            throw new ResourceNotFoundException(POST_NOT_FOUND);
        }
        Post post = optionalPost.get();

        // CategoryId
        Optional<Category> category = categoryRepository.findById(request.getCategoryId());
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

        postRepository.save(post);
    }
}
