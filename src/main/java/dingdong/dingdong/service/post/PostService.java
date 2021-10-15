package dingdong.dingdong.service.post;

import static dingdong.dingdong.util.exception.ResultCode.CATEGORY_NOT_FOUND;
import static dingdong.dingdong.util.exception.ResultCode.POST_NOT_FOUND;
import static dingdong.dingdong.util.exception.ResultCode.USER_NOT_FOUND;

import dingdong.dingdong.domain.chat.ChatJoinRepository;
import dingdong.dingdong.domain.chat.ChatPromiseRepository;
import dingdong.dingdong.domain.chat.ChatRoomRepository;
import dingdong.dingdong.domain.post.Category;
import dingdong.dingdong.domain.post.CategoryRepository;
import dingdong.dingdong.domain.post.Post;
import dingdong.dingdong.domain.post.PostRepository;
import dingdong.dingdong.domain.post.PostTag;
import dingdong.dingdong.domain.post.PostTagRepository;
import dingdong.dingdong.domain.post.Tag;
import dingdong.dingdong.domain.post.TagRepository;
import dingdong.dingdong.domain.user.User;
import dingdong.dingdong.domain.user.UserRepository;
import dingdong.dingdong.dto.post.PostDetailResponseDto;
import dingdong.dingdong.dto.post.PostGetResponseDto;
import dingdong.dingdong.dto.post.PostCreateRequestDto;
import dingdong.dingdong.dto.post.PostUpdateRequestDto;
import dingdong.dingdong.service.chat.ChatService;
import dingdong.dingdong.service.s3.S3Uploader;
import dingdong.dingdong.util.exception.ResourceNotFoundException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostTagRepository postTagRepository;
    private final TagRepository tagRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;

    private final ChatRoomRepository chatRoomRepository;
    private final ChatJoinRepository chatJoinRepository;
    private final ChatPromiseRepository chatPromiseRepository;

    private final ChatService chatService;

    // 유저의 LOCAL 정보에 기반하여 나누기 불러오기 (정렬 기준: 최신순)(홈화면)
    @Transactional(readOnly = true)
    public Page<PostGetResponseDto> findAllByCreateDateWithLocal(User user,
        Pageable pageable) {
        Page<Post> posts = postRepository.findAllByCreateDate(user.getLocal1().getId(), user.getLocal2().getId(), pageable);

        return posts.map(PostGetResponseDto::from);
    }

    // 유저의 LOCAL 정보에 기반하여 나누기 불러오기 (정렬 기준: 마감임박순)(홈화면)
    @Transactional(readOnly = true)
    public Page<PostGetResponseDto> findAllByEndDateWithLocal(User user,
        Pageable pageable) {
        Page<Post> posts = postRepository.findAllByEndDate(user.getLocal1().getId(), user.getLocal2().getId(), pageable);

        return posts.map(PostGetResponseDto::from);
    }

    // 원하는 나누기 피드 상세보기
    @Transactional(readOnly = true)
    public PostDetailResponseDto findPostById(Long id) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND));
        List<Tag> tags = postTagRepository.findTagByPost(post);

        return PostDetailResponseDto.from(post, tags);
    }

    // 유저의 LOCAL 정보에 기반하여 카테고리별로 나누기 불러오기 (정렬 기준: 마감임박순)(카테고리 화면)
    @Transactional(readOnly = true)
    public Page<PostGetResponseDto> findPostByCategoryIdWithLocal(User user,
        Long categoryId, Pageable pageable) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND));
        Page<Post> posts = postRepository
            .findByCategoryId(user.getLocal1().getId(), user.getLocal2().getId(), category.getId(), pageable);

        return posts.map(PostGetResponseDto::from);
    }

    // 유저의 판매내역 리스트 (GET: 유저별로 출력되는 나누기 피드)
    @Transactional(readOnly = true)
    public Page<PostGetResponseDto> findPostByUser(User user, Pageable pageable) {
        Page<Post> posts = postRepository.findByUserId(user.getId(), pageable);

        return posts.map(PostGetResponseDto::from);
    }

    // 특정 유저(본인 제외)가 생성한 나누기 피드들 불러오기
    @Transactional(readOnly = true)
    public Page<PostGetResponseDto> findPostByUserId(Long id, Pageable pageable) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));
        Page<Post> posts = postRepository.findByUserId(user.getId(), pageable);
        return posts.map(PostGetResponseDto::from);
    }

    // 유저의 구매내역 리스트 (GET: 유저별로 출력되는 나누기 피드)
    @Transactional(readOnly = true)
    public Page<PostGetResponseDto> findPostByUserIdOnChatJoin(User user, Pageable pageable) {
        Page<Post> posts = postRepository.findPostByUserIdOnChatJoin(user.getId(), pageable);

        return posts.map(PostGetResponseDto::from);
    }

    // 유저의 LOCAL 정보에 기반하지 않고 전체 나누기 불러오기 (정렬 기준: 최신순)(홈화면)(유저의 local 정보가 기입되지 않은 경우)
    @Transactional(readOnly = true)
    public Page<PostGetResponseDto> findAllByCreateDate(Pageable pageable) {
        Page<Post> postList = postRepository.findAllByCreateDateNotLocal(pageable);

        return postList.map(PostGetResponseDto::from);
    }

    // 유저의 LOCAL 정보에 기반하지 않고 전체 나누기 불러오기 (정렬 기준: 마감임박순)(홈화면)(유저의 local 정보가 기입되지 않은 경우)
    @Transactional(readOnly = true)
    public Page<PostGetResponseDto> findAllByEndDate(Pageable pageable) {
        Page<Post> posts = postRepository.findAllByEndDateNotLocal(pageable);

        return posts.map(PostGetResponseDto::from);
    }

    // 유저의 LOCAL 정보에 기반하여 카테고리별 나누기 불러오기 (정렬 기준: 최신순)(카테고리 화면)(유저의 local 정보가 기입된 경우)
    @Transactional(readOnly = true)
    public Page<PostGetResponseDto> findPostByCategoryId(Long categoryId, Pageable pageable) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND));
        Page<Post> posts = postRepository.findPostByCategoryIdNotLocal(category.getId(), pageable);

        return posts.map(PostGetResponseDto::from);
    }

    // 유저의 LOCAL 정보에 기반하여 카테고리별 나누기 불러오기 (정렬 기준: 마감임박순)(카테고리 화면)(유저의 local 정보가 기입된 경우)
    @Transactional(readOnly = true)
    public Page<PostGetResponseDto> findPostByCategoryIdSortByEndDateWithLocal(User user,
         Long categoryId, Pageable pageable) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND));
        Page<Post> posts = postRepository
            .findPostByCategoryIdSortByEndDate(user.getLocal1().getId(), user.getLocal2().getId(), category.getId(), pageable);

        return posts.map(PostGetResponseDto::from);
    }

    // 유저의 LOCAL 정보에 기반하지 않고 카테고리별 나누기 불러오기 (정렬 기준: 마감임박순)(카테고리 화면)(유저의 local 정보가 기입되지 않은 경우)
    @Transactional(readOnly = true)
    public Page<PostGetResponseDto> findPostByCategoryIdSortByEndDate(Long categoryId,
        Pageable pageable) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND));
        Page<Post> posts = postRepository
            .findPostByCategoryIdNotLocalSortByEndDate(category.getId(), pageable);

        return posts.map(PostGetResponseDto::from);
    }

    // 나누기 피드(post) 생성
    @Transactional
    public Long createPost(User user, PostCreateRequestDto postCreateRequestDto) {

        // CategoryId
        Category category = categoryRepository.findById(postCreateRequestDto.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND));

        List<String> paths = new ArrayList<>();
        // ImageList to S3
        if (postCreateRequestDto.getPostImages() != null) {
            List<MultipartFile> files = postCreateRequestDto.getPostImages();
            for (MultipartFile file : files) {
                paths.add(s3Uploader.upload(file, "static"));
            }
        }
        if (paths.size() < 3) {
            while (paths.size() < 3) {
                paths.add(
                        "https://dingdongbucket.s3.ap-northeast-2.amazonaws.com/static/default_post.png");
            }
        }

        // 나눔 저장
        Post post = Post.builder()
                .title(postCreateRequestDto.getTitle())
                .people(Integer.parseInt(postCreateRequestDto.getPeople()))
                .cost(Integer.parseInt(postCreateRequestDto.getCost()))
                .bio(postCreateRequestDto.getBio())
                .local(postCreateRequestDto.getLocal())
                .user(user)
                .category(category)
                .imageUrl1(paths.get(0))
                .imageUrl2(paths.get(1))
                .imageUrl3(paths.get(2))
                .build();
        postRepository.save(post);
        postRepository.flush();

        // Post PostTag 업로드
        String str = postCreateRequestDto.getPostTag();
        String[] array = (str.substring(1)).split("#");

        for (String s : array) {
            Tag tag = new Tag();
            if (!tagRepository.existsByName(s)) {
                tag.setName(s);
                tagRepository.save(tag);
                tagRepository.flush();
            } else {
                tag = tagRepository.findByName(s);
            }
            PostTag postTag = PostTag.builder()
                    .post(post)
                    .tag(tag)
                    .build();
            postTagRepository.save(postTag);
        }
        chatService.createChatRoom(post);
        return post.getId();
    }

    // 나누기 피드(post) 제거
    @Transactional
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND));
        postTagRepository.deleteByPostId(post.getId());

        if (chatPromiseRepository.existsById(id)) {
            chatPromiseRepository.deleteById(id);
        }
        if (chatRoomRepository.existsByPostId(id)) {
            chatJoinRepository.deleteByPostId(id);
            chatRoomRepository.deleteById(id);
        }
        postRepository.delete(post);
    }

    // 나누기 피드(post) 수정
    @Transactional
    public void updatePost(Long id, PostUpdateRequestDto postUpdateRequestDto) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND));

        if (postUpdateRequestDto.getTitle() != null){
            post.setTitle(postUpdateRequestDto.getTitle());
        }
        if (postUpdateRequestDto.getCost() != null){
            post.setCost(Integer.parseInt(postUpdateRequestDto.getCost()));
        }
        if (postUpdateRequestDto.getPeople() != null){
            post.setPeople(Integer.parseInt(postUpdateRequestDto.getPeople()));
        }
        if(postUpdateRequestDto.getBio() != null){
            post.setBio(postUpdateRequestDto.getBio());
        }
        if(postUpdateRequestDto.getLocal() != null){
            post.setLocal(postUpdateRequestDto.getLocal());
        }
        if(postUpdateRequestDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(postUpdateRequestDto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND));
            post.setCategory(category);
        }
        if(postUpdateRequestDto.getPostTag() != null){
            List<String> paths = new ArrayList<>();
            // ImageList to S3
            if (postUpdateRequestDto.getPostImages() != null) {
                // 이미지를 AWS S3에 업로드
                List<MultipartFile> files = postUpdateRequestDto.getPostImages();
                for (MultipartFile file : files) {
                    paths.add(s3Uploader.upload(file, "static"));
                }
                if (paths.size() < 3) {
                    while (paths.size() < 3) {
                        paths.add(
                                "https://dingdongbucket.s3.ap-northeast-2.amazonaws.com/static/default_post.png");
                    }
                }
        }

        }
        postRepository.save(post);
        // 나누기 PostTag Update
        if (postUpdateRequestDto.getPostTag() != null){
            postRepository.flush();
            String str = postUpdateRequestDto.getPostTag();
            String[] array = (str.substring(1)).split("#");

            postTagRepository.deleteByPostId(post.getId());

            for (String s : array) {
                Tag tag = new Tag();
                if (!tagRepository.existsByName(s)) {
                    tag.setName(s);
                    tagRepository.save(tag);
                    tagRepository.flush();
                } else {
                    tag = tagRepository.findByName(s);
                }
                PostTag postTag = PostTag.builder()
                        .post(post)
                        .tag(tag)
                        .build();
                postTagRepository.save(postTag);
            }
        }

    }

    // local 정보에 기반하지 않고 제목, 카테고리 검색 기능(검색 기능)(유저의 LOCAL 정보가 기입되지 않은 경우)
    @Transactional(readOnly = true)
    public Page<PostGetResponseDto> searchPosts(String keyword, Pageable pageable) {
        Page<Post> posts;
        if (keyword.contains("#")) {
            posts = postRepository.findAllSearchByTag(keyword.substring(1), pageable);
        } else {
            posts = postRepository.findAllSearch(keyword, pageable);
        }

        return posts.map(PostGetResponseDto::from);
    }

    // local 정보에 기반하여 제목, 카테고리 검색 기능(검색 기능)(유저의 LOCAL 정보가 기입된 경우)
    @Transactional(readOnly = true)
    public Page<PostGetResponseDto> searchPostsWithLocal(String keyword, Long local1, Long local2,
        Pageable pageable) {
        Page<Post> posts;
        if (keyword.contains("#")) {
            posts = postRepository
                .findAllSearchByTagWithLocal(keyword.substring(1), local1, local2, pageable);
        } else {
            posts = postRepository.findAllSearchWithLocal(keyword, local1, local2, pageable);
        }

        return posts.map(PostGetResponseDto::from);
    }
}
