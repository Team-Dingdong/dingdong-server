package dingdong.dingdong.service.post;

import dingdong.dingdong.domain.chat.ChatPromiseRepository;
import dingdong.dingdong.domain.chat.ChatRoomRepository;
import dingdong.dingdong.domain.post.*;
import dingdong.dingdong.domain.user.*;

import dingdong.dingdong.dto.post.PostRequestDto;
import dingdong.dingdong.dto.post.PostDetailResponseDto;
import dingdong.dingdong.dto.post.PostGetResponseDto;

import dingdong.dingdong.service.chat.ChatService;
import dingdong.dingdong.service.chatpromise.ChatPromiseService;
import dingdong.dingdong.util.exception.ForbiddenException;
import dingdong.dingdong.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static dingdong.dingdong.util.exception.ResultCode.*;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostTagRepository postTagRepository;
    private final CategoryRepository categoryRepository;
    private final ProfileRepository profileRepository;
    private final TagRepository tagRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatService chatService;
    private final ChatPromiseService chatPromiseService;
    private final ChatPromiseRepository chatPromiseRepository;

    // 홈화면 피드 GET(최신순으로 정렬)
    public Page<PostGetResponseDto> findAllByCreateDateWithLocal(Long local1, Long local2, Pageable pageable){
        Page<Post> postList = postRepository.findAllByCreateDate(local1, local2, pageable);

        Page<PostGetResponseDto> pagingList = postList.map(
            post -> PostGetResponseDto.from(post)
        );

        return pagingList;
    }

    // 홈화면 피드 GET(마감일자 순으로 정렬)
    public Page<PostGetResponseDto> findAllByEndDateWithLocal(Long local1, Long local2, Pageable pageable){
        Page<Post> postList = postRepository.findAllByEndDate(local1, local2, pageable);

        Page<PostGetResponseDto> pagingList = postList.map(
                post ->  PostGetResponseDto.from(post)
        );

        return pagingList;
    }

    // 나누기 피드 상세보기
    public PostDetailResponseDto findPostById(Long id){
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND));
        Profile profile = profileRepository.findByUserId(post.getUser().getId()).orElseThrow(() -> new ResourceNotFoundException(PROFILE_NOT_FOUND));
        List<String> tagList = tagRepository.findAllByPost_Id(id);

        PostDetailResponseDto postDetail = new PostDetailResponseDto(post, profile, tagList);
        return postDetail;

    }

    // 카테고리별로 나누기 피드 GET
    public Page<PostGetResponseDto> findPostByCategoryIdWithLocal(Long local1, Long local2, Long categoryId, Pageable pageable){
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND));
        Page<Post> postList = postRepository.findByCategoryId(local1, local2, category.getId(), pageable);

        Page<PostGetResponseDto> pagingList = postList.map(
            post -> PostGetResponseDto.from(post)
        );
        return pagingList;
    }

    // 유저의 판매내역 리스트 (GET: 유저별로 출력되는 나누기 피드)
    public Page<PostGetResponseDto> findPostByUserId(User user, Pageable pageable){
        Page<Post> postList = postRepository.findByUserId(user.getId(), pageable);

        Page<PostGetResponseDto> pagingList = postList.map(
            post -> PostGetResponseDto.from(post)
        );
        return pagingList;
    }

    // 유저의 구매내역 리스트 (GET: 유저별로 출력되는 나누기 피드)
    public Page<PostGetResponseDto> findPostByUserIdOnChatJoin(User user, Pageable pageable){
       Page<Post> postList = postRepository.findPostByUserIdOnChatJoin(user.getId(), pageable);

        Page<PostGetResponseDto> pagingList = postList.map(
                post -> PostGetResponseDto.from(post)
        );
        return pagingList;

    }

    // 홈화면 피드 GET(최신순으로 정렬)(유저의 local 정보가 없는 경우)
    public Page<PostGetResponseDto> findAllByCreateDate(Pageable pageable){
        Page<Post> postList = postRepository.findAllByCreateDateNotLocal(pageable);

        Page<PostGetResponseDto> pagingList = postList.map(
                post -> PostGetResponseDto.from(post)
        );
        return pagingList;
    }

    // 홈화면 피드 GET(마감임박순으로 정렬)(유저의 local 정보가 없는 경우)
    public Page<PostGetResponseDto> findAllByEndDate(Pageable pageable){
        Page<Post> postList = postRepository.findAllByEndDateNotLocal(pageable);

        Page<PostGetResponseDto> pagingList = postList.map(
                post -> PostGetResponseDto.from(post)
        );
        return pagingList;
    }

    // 카테고리화면 피드 GET(최신순으로 정렬)(유저의 local 정보가 없는 경우)
    public Page<PostGetResponseDto> findPostByCategoryId(Long categoryId, Pageable pageable){
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND));
        Page<Post> postList = postRepository.findPostByCategoryIdNotLocal(category.getId(),  pageable);

        Page<PostGetResponseDto> pagingList = postList.map(
                post -> PostGetResponseDto.from(post)
        );
        return pagingList;
    }

    // 카테고리화면 피드 GET(마감임박순으로 정렬)(유저의 local 정보에 기반하여 GET)
    public Page<PostGetResponseDto> findPostByCategoryIdSortByEndDateWithLocal(Long local1, Long local2,Long categoryId, Pageable pageable){
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND));
        Page<Post> postList = postRepository.findPostByCategoryIdSortByEndDate(local1, local2, category.getId(),  pageable);

        Page<PostGetResponseDto> pagingList = postList.map(
                post -> PostGetResponseDto.from(post)
        );
        return pagingList;
    }

    // 카테고리화면 피드 GET(마감임박순으로 정렬)(유저의 local 정보가 없는 경우)
    public Page<PostGetResponseDto> findPostByCategoryIdSortByEndDate(Long categoryId, Pageable pageable){
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND));
        Page<Post> postList = postRepository.findPostByCategoryIdNotLocalSortByEndDate(category.getId(),  pageable);

        Page<PostGetResponseDto> pagingList = postList.map(
                post -> PostGetResponseDto.from(post)
        );
        return pagingList;
    }

    // 나누기 피드(post) 생성
    @Transactional
    public Long createPost(User user, PostRequestDto request) {
        Post post = new Post();
        if(request == null) {
            throw new ForbiddenException(POST_CREATE_FAIL);
        }

        // CategoryId
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND));

        post.setImageUrl1("https://dingdongbucket.s3.ap-northeast-2.amazonaws.com/static/default_post.png");
        post.setImageUrl2("https://dingdongbucket.s3.ap-northeast-2.amazonaws.com/static/default_post.png");
        post.setImageUrl3("https://dingdongbucket.s3.ap-northeast-2.amazonaws.com/static/default_post.png");
        post.setPost(category, request);
        post.setUser(user);

        postRepository.save(post);
        postRepository.flush();

        String str = request.getPostTag();
        String[] array = (str.substring(1)).split("#");

        for(int i = 0; i < array.length; i++) {
            Tag tag = new Tag();
            if (!tagRepository.existsByName(array[i])) {
                tag.setName(array[i]);
                tagRepository.save(tag);
                tagRepository.flush();
            } else {
                tag = tagRepository.findByName(array[i]);
            }

            PostTag postTag = new PostTag();
            postTag.setPost(post);
            postTag.setTag(tag);
            postTagRepository.save(postTag);
        }

        chatService.createChatRoom(post);
        return post.getId();

    }
    
    // 나누기 피드(post) 제거
    public void  deletePost(Long id){
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND));
        postTagRepository.deleteByPost(post);
        chatPromiseRepository.deleteByPost(post);
        chatRoomRepository.deleteByPost(post);
    }

    // 나누기 피드(post) 수정
    public void updatePost(Long id, PostRequestDto request){
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND));

        // CategoryId
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND));

        post.setPost(category, request);
        postRepository.save(post);

        postRepository.flush();

        String str = request.getPostTag();
        String[] array = (str.substring(1)).split("#");

        postTagRepository.deleteByPost(post);

        for(int i = 0; i < array.length; i++) {
            Tag tag = new Tag();
            if (!tagRepository.existsByName(array[i])) {
                tag.setName(array[i]);
                tagRepository.save(tag);
                tagRepository.flush();
            } else {
                tag = tagRepository.findByName(array[i]);
            }

            PostTag postTag = new PostTag();
            postTag.setPost(post);
            postTag.setTag(tag);
            postTagRepository.save(postTag);
        }
    }


    public Page<PostGetResponseDto> searchPosts(String keyword,Pageable pageable){
        Page<Post> postList;
        if(keyword.contains("#")){
            postList = postRepository.findAllSearchByTag(keyword.substring(1), pageable);
        }else{
            postList = postRepository.findAllSearch(keyword, pageable);
        }

        Page<PostGetResponseDto> pagingList = postList.map(
                post -> PostGetResponseDto.from(post)
        );

        return pagingList;
    }
    // 제목, 카테고리 검색 기능(local 정보에 기반하여 검색)
    public Page<PostGetResponseDto> searchPostsWithLocal(String keyword, Long local1, Long local2, Pageable pageable){

        Page<Post> postList;
        if(keyword.contains("#")){
            postList = postRepository.findAllSearchByTagWithLocal(keyword.substring(1), local1, local2, pageable);
        }else{
            postList = postRepository.findAllSearchWithLocal(keyword, local1, local2, pageable);
        }

        Page<PostGetResponseDto> pagingList = postList.map(
                post -> PostGetResponseDto.from(post)
        );

        return pagingList;
    }

}
