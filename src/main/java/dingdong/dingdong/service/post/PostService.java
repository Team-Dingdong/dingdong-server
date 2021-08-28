package dingdong.dingdong.service.post;

import dingdong.dingdong.domain.post.*;
import dingdong.dingdong.domain.user.*;

import dingdong.dingdong.dto.post.PostRequestDto;
import dingdong.dingdong.dto.post.PostDetailResponseDto;
import dingdong.dingdong.dto.post.PostGetResponseDto;

import dingdong.dingdong.service.chat.ChatService;
import dingdong.dingdong.util.exception.ForbiddenException;
import dingdong.dingdong.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static dingdong.dingdong.util.exception.ResultCode.*;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostTagRepository postTagRepository;
    private final CategoryRepository categoryRepository;
    private final ProfileRepository profileRepository;
    private final TagRepository tagRepository;
    private final ChatService chatService;

    // 홈화면 피드 GET(최신순으로 정렬)
    public Page<PostGetResponseDto> findAllByCreateDate(Long local1, Long local2, Pageable pageable){
        Page<Post> postList = postRepository.findAllByCreateDate(local1, local2, pageable);

        Page<PostGetResponseDto> pagingList = postList.map(
            post -> PostGetResponseDto.from(post)
        );


        return pagingList;
    }

    // 홈화면 피드 GET(마감일자 순으로 정렬)
    public Page<PostGetResponseDto> findAllByEndDate(Long local1, Long local2, Pageable pageable){
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
        PostTag postTag = postTagRepository.findByPost(post).orElseThrow(() -> new ResourceNotFoundException(POSTTAG_NOT_FOUND));
        Tag tag = tagRepository.findById(postTag.getId()).orElseThrow(() -> new ResourceNotFoundException(TAG_NOT_FOUND));

        PostDetailResponseDto postDetail = new PostDetailResponseDto(post, profile, tag);
        return postDetail;
    }

    // 카테고리별로 나누기 피드 GET
    public Page<PostGetResponseDto> findPostByCategoryId(Long local1, Long local2, Long categoryId, Pageable pageable){
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND));
        Page<Post> postList = postRepository.findByCategoryId(local1, local2, category.getId(),  pageable);

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


    // 나누기 피드(post) 생성

    @Transactional
    public Long createPost(User user, PostRequestDto request) {
        Post post = new Post();
        if(request == null) {
            throw new ForbiddenException(POST_CREATE_FAIL);
        }

        // CategoryId
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND));

        post.setPost(category, request);
        post.setUser(user);

        postRepository.flush();

        String str = request.getPostTag();
        str.substring(1);
        String[] array = str.split("#");

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
        postRepository.deleteById(post.getId());
    }

    // 나누기 피드(post) 수정
    public void updatePost(Long id, PostRequestDto request){
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND));

        // CategoryId
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND));

        post.setPost(category, request);
        postRepository.save(post);
    }

    // 제목, 카테고리 검색 기능
    public Page<PostGetResponseDto> searchPosts(String keyword, Long local1, Long local2, Pageable pageable){

        Page<Post> postList;
        if(keyword.contains("#")){
            postList = postRepository.findAllSearchByTag(keyword.substring(1), local1, local2, pageable);
        }else{
            postList = postRepository.findAllSearch(keyword, local1, local2, pageable);
        }

        Page<PostGetResponseDto> pagingList = postList.map(
                post -> PostGetResponseDto.from(post)
        );

        return pagingList;
    }

}
