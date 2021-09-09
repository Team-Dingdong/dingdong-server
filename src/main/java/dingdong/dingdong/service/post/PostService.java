package dingdong.dingdong.service.post;

import dingdong.dingdong.domain.chat.ChatJoinRepository;
import dingdong.dingdong.domain.chat.ChatPromiseRepository;
import dingdong.dingdong.domain.chat.ChatRoomRepository;
import dingdong.dingdong.domain.post.*;
import dingdong.dingdong.domain.user.User;
import dingdong.dingdong.dto.post.PostDetailResponseDto;
import dingdong.dingdong.dto.post.PostGetResponseDto;
import dingdong.dingdong.dto.post.PostRequestDto;
import dingdong.dingdong.service.chat.ChatService;
import dingdong.dingdong.util.exception.ForbiddenException;
import dingdong.dingdong.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static dingdong.dingdong.util.exception.ResultCode.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostTagRepository postTagRepository;
    private final TagRepository tagRepository;
    private final CategoryRepository categoryRepository;

    private final ChatRoomRepository chatRoomRepository;
    private final ChatJoinRepository chatJoinRepository;
    private final ChatPromiseRepository chatPromiseRepository;

    private final ChatService chatService;

    // 홈화면 피드 GET(최신순으로 정렬)
    @Transactional
    public Page<PostGetResponseDto> findAllByCreateDateWithLocal(Long local1, Long local2, Pageable pageable){
        Page<Post> posts = postRepository.findAllByCreateDate(local1, local2, pageable);

        Page<PostGetResponseDto> data = posts.map(PostGetResponseDto::from);

        return data;
    }

    // 홈화면 피드 GET(마감일자 순으로 정렬)
    @Transactional
    public Page<PostGetResponseDto> findAllByEndDateWithLocal(Long local1, Long local2, Pageable pageable){
        Page<Post> posts = postRepository.findAllByEndDate(local1, local2, pageable);

        Page<PostGetResponseDto> data = posts.map(PostGetResponseDto::from);

        return data;
    }

    // 나누기 피드 상세보기
    @Transactional
    public PostDetailResponseDto findPostById(Long id){
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND));
        List<Tag> tags = postTagRepository.findTagByPost(post);

        return PostDetailResponseDto.from(post, tags);
    }

    // 카테고리별로 나누기 피드 GET
    @Transactional
    public Page<PostGetResponseDto> findPostByCategoryIdWithLocal(Long local1, Long local2, Long categoryId, Pageable pageable){
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND));
        Page<Post> posts = postRepository.findByCategoryId(local1, local2, category.getId(), pageable);

        Page<PostGetResponseDto> data = posts.map(PostGetResponseDto::from);

        return data;
    }

    // 유저의 판매내역 리스트 (GET: 유저별로 출력되는 나누기 피드)
    @Transactional
    public Page<PostGetResponseDto> findPostByUserId(User user, Pageable pageable){
        Page<Post> posts = postRepository.findByUserId(user.getId(), pageable);

        Page<PostGetResponseDto> data = posts.map(PostGetResponseDto::from);

        return data;
    }

    // 유저의 구매내역 리스트 (GET: 유저별로 출력되는 나누기 피드)
    @Transactional
    public Page<PostGetResponseDto> findPostByUserIdOnChatJoin(User user, Pageable pageable){
        Page<Post> posts = postRepository.findPostByUserIdOnChatJoin(user.getId(), pageable);

        Page<PostGetResponseDto> data = posts.map(PostGetResponseDto::from);

        return data;
    }

    // 홈화면 피드 GET(최신순으로 정렬)(유저의 local 정보가 없는 경우)
    @Transactional
    public Page<PostGetResponseDto> findAllByCreateDate(Pageable pageable){
        Page<Post> postList = postRepository.findAllByCreateDateNotLocal(pageable);

        Page<PostGetResponseDto> data = postList.map(PostGetResponseDto::from);

        return data;
    }

    // 홈화면 피드 GET(마감임박순으로 정렬)(유저의 local 정보가 없는 경우)
    @Transactional
    public Page<PostGetResponseDto> findAllByEndDate(Pageable pageable){
        Page<Post> posts = postRepository.findAllByEndDateNotLocal(pageable);

        Page<PostGetResponseDto> data = posts.map(PostGetResponseDto::from);

        return data;
    }

    // 카테고리화면 피드 GET(최신순으로 정렬)(유저의 local 정보가 없는 경우)
    @Transactional
    public Page<PostGetResponseDto> findPostByCategoryId(Long categoryId, Pageable pageable){
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND));
        Page<Post> posts = postRepository.findPostByCategoryIdNotLocal(category.getId(), pageable);

        Page<PostGetResponseDto> data = posts.map(PostGetResponseDto::from);

        return data;
    }

    // 카테고리화면 피드 GET(마감임박순으로 정렬)(유저의 local 정보에 기반하여 GET)
    @Transactional
    public Page<PostGetResponseDto> findPostByCategoryIdSortByEndDateWithLocal(Long local1, Long local2,Long categoryId, Pageable pageable){
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND));
        Page<Post> posts = postRepository.findPostByCategoryIdSortByEndDate(local1, local2, category.getId(),  pageable);

        Page<PostGetResponseDto> data = posts.map(PostGetResponseDto::from);

        return data;
    }

    // 카테고리화면 피드 GET(마감임박순으로 정렬)(유저의 local 정보가 없는 경우)
    @Transactional
    public Page<PostGetResponseDto> findPostByCategoryIdSortByEndDate(Long categoryId, Pageable pageable){
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND));
        Page<Post> posts = postRepository.findPostByCategoryIdNotLocalSortByEndDate(category.getId(),  pageable);

        Page<PostGetResponseDto> data = posts.map(PostGetResponseDto::from);

        return data;
    }

    // 나누기 피드(post) 생성
    @Transactional
    public Long createPost(User user, PostRequestDto postRequestDto) {
        Post post = new Post();
        if(postRequestDto == null) {
            throw new ForbiddenException(POST_CREATE_FAIL);
        }

        // CategoryId
        Category category = categoryRepository.findById(postRequestDto.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND));

        post.setImageUrl1("https://dingdongbucket.s3.ap-northeast-2.amazonaws.com/static/default_post.png");
        post.setImageUrl2("https://dingdongbucket.s3.ap-northeast-2.amazonaws.com/static/default_post.png");
        post.setImageUrl3("https://dingdongbucket.s3.ap-northeast-2.amazonaws.com/static/default_post.png");
        post.setPost(category, postRequestDto);
        post.setUser(user);

        postRepository.save(post);
        postRepository.flush();

        String str = postRequestDto.getPostTag();
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

        if(chatPromiseRepository.existsById(id)){
            chatPromiseRepository.deleteById(id);
        }
        if(chatRoomRepository.existsByPostId(id)){
            chatJoinRepository.deleteByPost_id(id);
            chatRoomRepository.deleteById(id);
        }

        postRepository.deleteByPostId(id);
    }

    // 나누기 피드(post) 수정
    @Transactional
    public void updatePost(Long id, PostRequestDto postRequestDto){
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND));

        // CategoryId
        Category category = categoryRepository.findById(postRequestDto.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND));

        post.setPost(category, postRequestDto);
        postRepository.save(post);

        postRepository.flush();

        String str = postRequestDto.getPostTag();
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

    @Transactional
    public Page<PostGetResponseDto> searchPosts(String keyword,Pageable pageable){
        Page<Post> posts;
        if(keyword.contains("#")){
            posts = postRepository.findAllSearchByTag(keyword.substring(1), pageable);
        }else{
            posts = postRepository.findAllSearch(keyword, pageable);
        }

        Page<PostGetResponseDto> data = posts.map(PostGetResponseDto::from);

        return data;
    }

    // 제목, 카테고리 검색 기능(local 정보에 기반하여 검색)
    @Transactional
    public Page<PostGetResponseDto> searchPostsWithLocal(String keyword, Long local1, Long local2, Pageable pageable){
        Page<Post> posts;
        if(keyword.contains("#")){
            posts = postRepository.findAllSearchByTagWithLocal(keyword.substring(1), local1, local2, pageable);
        }else{
            posts = postRepository.findAllSearchWithLocal(keyword, local1, local2, pageable);
        }

        Page<PostGetResponseDto> data = posts.map(PostGetResponseDto::from);

        return data;
    }
}
