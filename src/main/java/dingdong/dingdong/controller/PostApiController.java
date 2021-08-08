package dingdong.dingdong.controller;


import dingdong.dingdong.domain.post.CategoryRepository;
import dingdong.dingdong.domain.post.Post;
import dingdong.dingdong.domain.user.RatingRepository;
import dingdong.dingdong.domain.user.UserRepository;
import dingdong.dingdong.dto.Post.PostCreationRequest;
import dingdong.dingdong.dto.Post.PostDetailResponse;
import dingdong.dingdong.dto.Post.PostGetResponse;
import dingdong.dingdong.dto.Post.PostUpdateRequest;
import dingdong.dingdong.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostApiController {

    private final PostService postService;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RatingRepository ratingRepository;

    // 모든 나누기 불러오기
    @GetMapping("/")
    public Page<PostGetResponse> findPosts(@PageableDefault(size = 3, direction = Sort.Direction.DESC) Pageable pageable) {
        return postService.findPosts(pageable);
    }
    // 몇 개씩 줄 지 상의(페이징 넣기)

    // 특정 나누기 상세보기 불러오기
    @GetMapping("/{id}")
    public PostDetailResponse findPostById(@PathVariable Long id){

        return postService.findPostById(id);
    }

    // 카테고리 별로 나누기 피드들 불러오기
    @GetMapping("/category/{id}")
    public Page<PostGetResponse> findPostByCategory_Id(@PathVariable Long id,
                                            @PageableDefault(size = 3, direction = Sort.Direction.DESC) Pageable pageable){
        return postService.findPostByCategory_Id(id, pageable);
    }

    // 나누기 삭제
   @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id){
       postService.deletePost(id);
    }

    // 나누기 생성
    @PostMapping("/")
    public void createPost(@Valid @RequestBody PostCreationRequest request) {
        postService.createPost(request);
    }

    // 나누기 수정
    @PatchMapping("/{id}")
    public void updatePost(@RequestBody PostUpdateRequest request,
                                           @PathVariable Long id){
        postService.updatePost(id, request);
    }
}
