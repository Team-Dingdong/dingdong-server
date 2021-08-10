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
import dingdong.dingdong.util.exception.Result;
import dingdong.dingdong.util.exception.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostApiController {

    private final PostService postService;

    // 모든 나누기 불러오기
    @GetMapping("")
    public ResponseEntity<?> findPosts(@PageableDefault(size = 5, direction = Sort.Direction.DESC) Pageable pageable) {

            Page<PostGetResponse> pagingList = postService.findPosts(pageable);
            return Result.toResult(ResultCode.POST_READ_SUCCESS, pagingList);
    }

    // 특정 나누기 상세보기 불러오기
    @GetMapping("/{id}")
    public ResponseEntity<?> findPostById(@PathVariable Long id){

            PostDetailResponse postDetail = postService.findPostById(id);
            ResultCode message = ResultCode.POST_READ_SUCCESS;
            return Result.toResult(message, postDetail);
    }

    // 카테고리 별로 나누기 피드들 불러오기
    @GetMapping("/category/{id}")
    public ResponseEntity<?> findPostByCategory_Id(@PathVariable Long id,
                                            @PageableDefault(size = 5, direction = Sort.Direction.DESC) Pageable pageable){

            Page<PostGetResponse> postPage = postService.findPostByCategory_Id(id, pageable);
            return Result.toResult(ResultCode.POST_READ_SUCCESS, postPage);

    }

    // 나누기 생성
    @PostMapping("")
    public ResponseEntity<?> createPost(@Valid @RequestBody PostCreationRequest request) {

            postService.createPost(request);
            return Result.toResult(ResultCode.POST_CREATE_SUCCESS);

    }

    // 나누기 삭제
   @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id){

           postService.deletePost(id);
           return Result.toResult(ResultCode.POST_DELETE_SUCCESS);
    }

    // 나누기 수정
    @PatchMapping("/{id}")
    public ResponseEntity<?> updatePost(@RequestBody PostUpdateRequest request,
                                           @PathVariable Long id){
            postService.updatePost(id, request);
            return Result.toResult(ResultCode.POST_UPDATE_SUCCESS);
    }
}
