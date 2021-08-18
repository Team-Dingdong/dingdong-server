package dingdong.dingdong.controller;


import dingdong.dingdong.dto.post.PostCreationRequest;
import dingdong.dingdong.dto.post.PostDetailResponse;
import dingdong.dingdong.dto.post.PostGetResponse;
import dingdong.dingdong.dto.post.PostUpdateRequest;
import dingdong.dingdong.service.post.PostService;
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
public class PostController {

    private final PostService postService;

    // 모든 나누기 불러오기
    @GetMapping("")
    public ResponseEntity<Result<Page<PostGetResponse>>> findPosts(@PageableDefault(size = 5, direction = Sort.Direction.DESC) Pageable pageable) {
            Page<PostGetResponse> data = postService.findPosts(pageable);
            return Result.toResult(ResultCode.POST_READ_SUCCESS, data);
    }

    // 특정 나누기 상세보기 불러오기
    @GetMapping("/{id}")
    public ResponseEntity<Result<PostDetailResponse>> findPostById(@PathVariable Long id){
            PostDetailResponse data = postService.findPostById(id);
            return Result.toResult(ResultCode.POST_READ_SUCCESS, data);
    }

    // 카테고리 별로 나누기 피드들 불러오기
    @GetMapping("/category/{id}")
    public ResponseEntity<Result<Page<PostGetResponse>>> findPostByCategory_Id(@PathVariable Long id, @PageableDefault(size = 5, direction = Sort.Direction.DESC) Pageable pageable){
            Page<PostGetResponse> data = postService.findPostByCategory_Id(id, pageable);
            return Result.toResult(ResultCode.POST_READ_SUCCESS, data);

    }

    // 나누기 생성
    @PostMapping("")
    public ResponseEntity<Result> createPost(@Valid @RequestBody PostCreationRequest request) {
            postService.createPost(request);
            return Result.toResult(ResultCode.POST_CREATE_SUCCESS);

    }

    // 나누기 삭제
   @DeleteMapping("/{id}")
    public ResponseEntity<Result> deletePost(@PathVariable Long id){

           postService.deletePost(id);
           return Result.toResult(ResultCode.POST_DELETE_SUCCESS);
    }

    // 나누기 수정
    @PatchMapping("/{id}")
    public ResponseEntity<Result> updatePost(@RequestBody PostUpdateRequest request, @PathVariable Long id){
            postService.updatePost(id, request);
            return Result.toResult(ResultCode.POST_UPDATE_SUCCESS);
    }
}
