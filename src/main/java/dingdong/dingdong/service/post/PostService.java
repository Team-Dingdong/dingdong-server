package dingdong.dingdong.service.post;

import static dingdong.dingdong.util.exception.ResultCode.CATEGORY_NOT_FOUND;
import static dingdong.dingdong.util.exception.ResultCode.LOCAL_NOT_FOUND;
import static dingdong.dingdong.util.exception.ResultCode.POST_DELETE_FAIL_DONE;
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
import dingdong.dingdong.domain.user.Local;
import dingdong.dingdong.domain.user.LocalRepository;
import dingdong.dingdong.domain.user.User;
import dingdong.dingdong.domain.user.UserRepository;
import dingdong.dingdong.dto.post.PostDetailResponseDto;
import dingdong.dingdong.dto.post.PostGetResponseDto;
import dingdong.dingdong.dto.post.PostCreateRequestDto;
import dingdong.dingdong.dto.post.PostUpdateRequestDto;
import dingdong.dingdong.service.chat.ChatService;
import dingdong.dingdong.service.s3.S3Uploader;
import dingdong.dingdong.util.exception.ForbiddenException;
import dingdong.dingdong.util.exception.ResourceNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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

    // 유저의 LOCAL 정보에 기반하여 나누기 불러오기 (정렬 기준: 최신순)(홈화면)(유저의 local 정보 기반)
    @Transactional(readOnly = true)
    public Page<PostGetResponseDto> findAllByCreateDateWithLocal(User user, Long localId,
        Pageable pageable) {
        Page<Post> posts;
        if (localId == 1L){
            posts = postRepository.findAllByCreateDateWithLocal(user.getLocal1().getId(), pageable);
        }else if(localId == 2L){
            posts = postRepository.findAllByCreateDateWithLocal(user.getLocal2().getId(), pageable);
        }else{
            throw new ResourceNotFoundException(LOCAL_NOT_FOUND);
        }

        Page<PostGetResponseDto> data = posts.map(PostGetResponseDto::from);
        for (PostGetResponseDto dto : data){
            Post post = postRepository.findById(dto.getId()).orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND));
            List<Tag> tags = postTagRepository.findTagByPost(post);
            List<String> T = new ArrayList<>();
            for (Tag t: tags){
                T.add(t.getName());
            }
            dto.setTags(T);
        }
        return data;
    }

    // 유저의 LOCAL 정보에 기반하여 나누기 불러오기 (정렬 기준: 마감임박순)(홈화면)(유저의 local 정보 기반)
    @Transactional(readOnly = true)
    public Page<PostGetResponseDto> findAllByEndDateWithLocal(User user, Long localId,
        Pageable pageable) {
        Page<Post> posts;
        if (localId == 1L){
            posts = postRepository.findAllByEndDateWithLocal(user.getLocal1().getId(), pageable);
        }else if(localId == 2L){
            posts = postRepository.findAllByEndDateWithLocal(user.getLocal2().getId(), pageable);
        }else{
            throw new ResourceNotFoundException(LOCAL_NOT_FOUND);
        }

        Page<PostGetResponseDto> data = posts.map(PostGetResponseDto::from);
        for (PostGetResponseDto dto : data){
            Post post = postRepository.findById(dto.getId()).orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND));
            List<Tag> tags = postTagRepository.findTagByPost(post);
            List<String> T = new ArrayList<>();
            for (Tag t: tags){
                T.add(t.getName());
            }
            dto.setTags(T);
        }
        return data;
    }

    // 유저의 LOCAL 정보에 기반하여 카테고리별로 나누기 불러오기 (정렬 기준: 최신순)(카테고리 화면)(유저의 local 정보 기반)
    @Transactional(readOnly = true)
    public Page<PostGetResponseDto> findPostByCategoryIdWithLocal(User user,
        Long categoryId, Long localId, Pageable pageable) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND));
        Page<Post> posts;
        if (localId == 1L){
            posts = postRepository.findPostByCategoryIdWithLocal(category.getId(), user.getLocal1().getId(), pageable);
        }else if(localId == 2L){
            posts = postRepository.findPostByCategoryIdWithLocal(category.getId(), user.getLocal2().getId(), pageable);
        }else{
            throw new ResourceNotFoundException(LOCAL_NOT_FOUND);
        }

        Page<PostGetResponseDto> data = posts.map(PostGetResponseDto::from);
        for (PostGetResponseDto dto : data){
            Post post = postRepository.findById(dto.getId()).orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND));
            List<Tag> tags = postTagRepository.findTagByPost(post);
            List<String> T = new ArrayList<>();
            for (Tag t: tags){
                T.add(t.getName());
            }
            dto.setTags(T);
        }
        return data;
    }

    // 유저의 LOCAL 정보에 기반하여 카테고리별 나누기 불러오기 (정렬 기준: 마감임박순)(카테고리 화면)(유저의 local 정보 기반)
    @Transactional(readOnly = true)
    public Page<PostGetResponseDto> findPostByCategoryIdSortByEndDateWithLocal(User user,
        Long categoryId, Long localId, Pageable pageable) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND));
        Page<Post> posts;
        if (localId == 1L){
            posts = postRepository.findPostByCategoryIdSortByEndDateWithLocal(category.getId(),
                user.getLocal1().getId(), pageable);
        }else if(localId == 2L){
            posts = postRepository.findPostByCategoryIdSortByEndDateWithLocal(category.getId(),
                user.getLocal2().getId(), pageable);
        }else{
            throw new ResourceNotFoundException(LOCAL_NOT_FOUND);
        }

        Page<PostGetResponseDto> data = posts.map(PostGetResponseDto::from);
        for (PostGetResponseDto dto : data){
            Post post = postRepository.findById(dto.getId()).orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND));
            List<Tag> tags = postTagRepository.findTagByPost(post);
            List<String> T = new ArrayList<>();
            for (Tag t: tags){
                T.add(t.getName());
            }
            dto.setTags(T);
        }
        return data;
    }

    // 원하는 나누기 피드 상세보기
    @Transactional(readOnly = true)
    public PostDetailResponseDto findPostById(Long id) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND));
        List<Tag> tags = postTagRepository.findTagByPost(post);

        return PostDetailResponseDto.from(post, tags);
    }

    // 유저의 판매내역 리스트 (GET: 유저별로 출력되는 나누기 피드)
    @Transactional(readOnly = true)
    public List<PostGetResponseDto> findPostByUser(User user) {
        List<Post> posts = postRepository.findByUserId(user.getId());

        List<PostGetResponseDto> data = posts.stream().map(PostGetResponseDto::from).collect(Collectors.toList());
        for (PostGetResponseDto dto : data){
            Post post = postRepository.findById(dto.getId()).orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND));
            List<Tag> tags = postTagRepository.findTagByPost(post);
            List<String> T = new ArrayList<>();
            for (Tag t: tags){
                T.add(t.getName());
            }
            dto.setTags(T);
        }
        return data;
    }

    // 특정 유저(본인 제외)가 생성한 나누기 피드들 불러오기
    @Transactional(readOnly = true)
    public Page<PostGetResponseDto> findPostByUserId(Long id, Pageable pageable) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));
        Page<Post> posts = postRepository.findByUserIdPaging(user.getId(), pageable);

        Page<PostGetResponseDto> data = posts.map(PostGetResponseDto::from);
        for (PostGetResponseDto dto : data){
            Post post = postRepository.findById(dto.getId()).orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND));
            List<Tag> tags = postTagRepository.findTagByPost(post);
            List<String> T = new ArrayList<>();
            for (Tag t: tags){
                T.add(t.getName());
            }
            dto.setTags(T);
        }
        return data;
    }

    // 유저의 구매내역 리스트 (GET: 유저별로 출력되는 나누기 피드)
    @Transactional(readOnly = true)
    public List<PostGetResponseDto> findPostByUserIdOnChatJoin(User user) {
        List<Post> posts = postRepository.findPostByUserIdOnChatJoin(user.getId());

        List<PostGetResponseDto> data = posts.stream().map(PostGetResponseDto::from).collect(Collectors.toList());
        for (PostGetResponseDto dto : data){
            Post post = postRepository.findById(dto.getId()).orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND));
            List<Tag> tags = postTagRepository.findTagByPost(post);
            List<String> T = new ArrayList<>();
            for (Tag t: tags){
                T.add(t.getName());
            }
            dto.setTags(T);
        }
        return data;
    }

    // 유저의 LOCAL 정보에 기반하지 않고 전체 나누기 불러오기 (정렬 기준: 최신순)(홈화면)(local 정보를 무시)
    @Transactional(readOnly = true)
    public Page<PostGetResponseDto> findPostsSortByCreatedDateNotLocal(Pageable pageable) {
        Page<Post> posts = postRepository.findPostsSortByCreatedDateNotLocal(pageable);

        Page<PostGetResponseDto> data = posts.map(PostGetResponseDto::from);
        for (PostGetResponseDto dto : data){
            Post post = postRepository.findById(dto.getId()).orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND));
            List<Tag> tags = postTagRepository.findTagByPost(post);
            List<String> T = new ArrayList<>();
            for (Tag t: tags){
                T.add(t.getName());
            }
            dto.setTags(T);
        }
        return data;
    }

    // 유저의 LOCAL 정보에 기반하지 않고 전체 나누기 불러오기 (정렬 기준: 마감임박순)(홈화면)(local 정보를 무시)
    @Transactional(readOnly = true)
    public Page<PostGetResponseDto> findPostsSortByEndDateNotLocal(Pageable pageable) {
        Page<Post> posts = postRepository.findPostsSortByEndDateNotLocal(pageable);

        Page<PostGetResponseDto> data = posts.map(PostGetResponseDto::from);
        for (PostGetResponseDto dto : data){
            Post post = postRepository.findById(dto.getId()).orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND));
            List<Tag> tags = postTagRepository.findTagByPost(post);
            List<String> T = new ArrayList<>();
            for (Tag t: tags){
                T.add(t.getName());
            }
            dto.setTags(T);
        }
        return data;
    }

    // 유저의 LOCAL 정보에 기반하여 카테고리별 나누기 불러오기 (정렬 기준: 최신순)(카테고리 화면)(local 정보를 무시)
    @Transactional(readOnly = true)
    public Page<PostGetResponseDto> findPostByCategoryIdSortByCreatedDateNotLocal(Long categoryId, Pageable pageable) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND));
        Page<Post> posts = postRepository.findPostByCategoryIdSortByCreatedDateNotLocal(category.getId(), pageable);

        Page<PostGetResponseDto> data = posts.map(PostGetResponseDto::from);
        for (PostGetResponseDto dto : data){
            Post post = postRepository.findById(dto.getId()).orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND));
            List<Tag> tags = postTagRepository.findTagByPost(post);
            List<String> T = new ArrayList<>();
            for (Tag t: tags){
                T.add(t.getName());
            }
            dto.setTags(T);
        }
        return data;
    }

    // 유저의 LOCAL 정보에 기반하지 않고 카테고리별 나누기 불러오기 (정렬 기준: 마감임박순)(카테고리 화면)(local 정보를 무시)
    @Transactional(readOnly = true)
    public Page<PostGetResponseDto> findPostByCategoryIdSortByEndDateNotLocal(Long categoryId,
        Pageable pageable) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND));
        Page<Post> posts = postRepository
            .findPostByCategoryIdSortByEndDateNotLocal(category.getId(), pageable);

        Page<PostGetResponseDto> data = posts.map(PostGetResponseDto::from);
        for (PostGetResponseDto dto : data){
            Post post = postRepository.findById(dto.getId()).orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND));
            List<Tag> tags = postTagRepository.findTagByPost(post);
            List<String> T = new ArrayList<>();
            for (Tag t: tags){
                T.add(t.getName());
            }
            dto.setTags(T);
        }
        return data;
    }

    // 나누기 피드(post) 생성
    @Transactional
    public Long createPost(User user, Long localId, PostCreateRequestDto postCreateRequestDto) {

        // CategoryId
        Category category = categoryRepository.findById(postCreateRequestDto.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND));

        Local local;
        if (localId == 1L){
            local = user.getLocal1();
        } else if (localId == 2L){
            local = user.getLocal2();
        }else{
            throw new ResourceNotFoundException(LOCAL_NOT_FOUND);
        }

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
                .location(postCreateRequestDto.getLocation())
                .user(user)
                .category(category)
                .imageUrl1(paths.get(0))
                .imageUrl2(paths.get(1))
                .imageUrl3(paths.get(2))
                .local(local)
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

        if(post.getDone() == Boolean.TRUE){
            postTagRepository.deleteByPostId(post.getId());

            if (chatPromiseRepository.existsById(id)) {
                chatPromiseRepository.deleteById(id);
            }
            if (chatRoomRepository.existsByPostId(id)) {
                chatJoinRepository.deleteByPostId(id);
                chatRoomRepository.deleteById(id);
            }
            postRepository.deletePostById(post.getId());
        } else {
            throw new ForbiddenException(POST_DELETE_FAIL_DONE);
        }
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
        if(postUpdateRequestDto.getLocation() != null){
            post.setLocation(postUpdateRequestDto.getLocation());
        }
        if(postUpdateRequestDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(postUpdateRequestDto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND));
            post.setCategory(category);
        }

        // ImageList to S3
        List<String> paths = new ArrayList<>();
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
            post.setImageUrl(paths.get(0), paths.get(1), paths.get(2));
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

        Page<PostGetResponseDto> data = posts.map(PostGetResponseDto::from);
        for (PostGetResponseDto dto : data){
            Post post = postRepository.findById(dto.getId()).orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND));
            List<Tag> tags = postTagRepository.findTagByPost(post);
            List<String> T = new ArrayList<>();
            for (Tag t: tags){
                T.add(t.getName());
            }
            dto.setTags(T);
        }
        return data;
    }

    // local 정보에 기반하여 제목, 카테고리 검색 기능(검색 기능)(유저의 LOCAL 정보가 기입된 경우)
    @Transactional(readOnly = true)
    public Page<PostGetResponseDto> searchPostsWithLocal(String keyword, User user,
        Pageable pageable) {
        Page<Post> posts;
        if (user.getLocal1() == null & user.getLocal2() == null){
            if (keyword.contains("#")) {
                posts = postRepository.findAllSearchByTag(keyword.substring(1), pageable);
            } else {
                posts = postRepository.findAllSearch(keyword, pageable);
            }
        }else{
            if (keyword.contains("#")) {
                posts = postRepository
                    .findAllSearchByTagWithLocal(keyword.substring(1), user.getLocal1().getId(), user.getLocal2().getId(), pageable);
            } else {
                posts = postRepository.findAllSearchWithLocal(keyword, user.getLocal1().getId(), user.getLocal2().getId(), pageable);
            }
        }

        Page<PostGetResponseDto> data = posts.map(PostGetResponseDto::from);
        for (PostGetResponseDto dto : data){
            Post post = postRepository.findById(dto.getId()).orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND));
            List<Tag> tags = postTagRepository.findTagByPost(post);
            List<String> T = new ArrayList<>();
            for (Tag t: tags){
                T.add(t.getName());
            }
            dto.setTags(T);
        }
        return data;
    }
}
