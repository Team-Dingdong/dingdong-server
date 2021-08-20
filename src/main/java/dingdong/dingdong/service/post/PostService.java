package dingdong.dingdong.service.post;

import dingdong.dingdong.domain.post.Category;
import dingdong.dingdong.domain.post.CategoryRepository;
import dingdong.dingdong.domain.post.Post;
import dingdong.dingdong.domain.post.PostRepository;
import dingdong.dingdong.domain.user.*;

import dingdong.dingdong.dto.post.PostCreationRequestDto;
import dingdong.dingdong.dto.post.PostDetailResponseDto;
import dingdong.dingdong.dto.post.PostGetResponseDto;
import dingdong.dingdong.dto.post.PostUpdateRequestDto;

import dingdong.dingdong.util.exception.ForbiddenException;
import dingdong.dingdong.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static dingdong.dingdong.util.exception.ResultCode.*;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
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
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND));
        Profile profile = profileRepository.findByUserId(post.getUser().getId()).orElseThrow(() -> new ResourceNotFoundException(PROFILE_NOT_FOUND));

        PostDetailResponseDto postDetail = new PostDetailResponseDto(post, profile);
        return postDetail;
    }

    // 카테고리별로 나누기 피드 GET
    public Page<PostGetResponseDto> findPostByCategoryId(Long local1, Long local2, Long categoryId, Pageable pageable){
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND));
        Page<Post> postList = postRepository.findByCategoryId(local1, local2, category.getId(),  pageable);

        Page<PostGetResponseDto> pagingList = postList.map(
            post -> new PostGetResponseDto(
                post.getTitle(), post.getPeople(), post.getCost(),
                post.getBio(), post.getImageUrl(), post.getLocal(),
                post.getCreatedDate()
            ));
        return pagingList;
    }

    // 유저의 판매내역 리스트 (GET: 유저별로 출력되는 나누기 피드)
    public Page<PostGetResponseDto> findPostByUserId(User user, Pageable pageable){
        Page<Post> postList = postRepository.findByUserId(user.getId(), pageable);

        Page<PostGetResponseDto> pagingList = postList.map(
            post -> new PostGetResponseDto(
                post.getTitle(), post.getPeople(), post.getCost(),
                post.getBio(), post.getImageUrl(), post.getLocal(),
                post.getCreatedDate()
            ));
        return pagingList;
    }


    // 나누기 피드(post) 생성
    public void createPost(User user, PostCreationRequestDto request) {
        Post post = new Post();
        if(request == null) {
            throw new ForbiddenException(POST_CREATE_FAIL);
        }

        // CategoryId
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND));
        post.setCategory(category);

        // UserId
        post.setUser(user);

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
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND));
        postRepository.deleteById(post.getId());
    }

    // 나누기 피드(post) 수정
    public void updatePost(Long id, PostUpdateRequestDto request){
        Post optionalPost = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND));
        Post post = optionalPost;

        // CategoryId
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND));
        post.setCategory(category);

        post.setTitle(request.getTitle());
        post.setPeople(request.getPeople());
        post.setCost(request.getCost());
        post.setBio(request.getBio());
        post.setLocal(request.getLocal());
        post.setImageUrl(request.getImageUrl());

        postRepository.save(post);
    }
}
