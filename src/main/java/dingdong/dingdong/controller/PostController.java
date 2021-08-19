package dingdong.dingdong.controller;



import dingdong.dingdong.service.s3.S3Uploader;
import dingdong.dingdong.domain.user.*;
import dingdong.dingdong.dto.Post.PostCreationRequestDto;
import dingdong.dingdong.dto.Post.PostDetailResponseDto;
import dingdong.dingdong.dto.Post.PostGetResponseDto;
import dingdong.dingdong.dto.Post.PostUpdateRequestDto;
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
    public ResponseEntity<Result<Page<PostGetResponseDto>>>  findPosts(@CurrentUser User user, @PageableDefault(size = 5, direction = Sort.Direction.DESC) Pageable pageable) {

            Long local1 = user.getLocal1().getId();
            Long local2 = user.getLocal2().getId();
            Page<PostGetResponseDto> data = postService.findPosts(local1, local2, pageable);
            return Result.toResult(ResultCode.POST_READ_SUCCESS, data);
    }

    // 특정 나누기 상세보기 불러오기
    @GetMapping("/{post_id}")
    public ResponseEntity<Result<PostDetailResponseDto>> findPostById(@PathVariable Long post_id) {

        PostDetailResponseDto data = postService.findPostById(post_id);
        ResultCode message = ResultCode.POST_READ_SUCCESS;
        return Result.toResult(message, data);

    }

    // 카테고리 별로 나누기 피드들 불러오기
    @GetMapping("/category/{id}")
    public ResponseEntity<Result<Page<PostGetResponseDto>>>  findPostByCategory_Id(@CurrentUser User user, @PathVariable Long id,
                                            @PageableDefault(size = 5, direction = Sort.Direction.DESC) Pageable pageable){
            Long local1 = user.getLocal1().getId();
            Long local2 = user.getLocal2().getId();
            Page<PostGetResponseDto> data = postService.findPostByCategory_Id(local1, local2, id, pageable);
            return Result.toResult(ResultCode.POST_READ_SUCCESS, data);
    }

    // 유저 별로 나누기 피드들 불러오기
    @GetMapping("/user")
    public ResponseEntity<Result<Page<PostGetResponseDto>>> findPostByUser_Id(@CurrentUser User user, @PageableDefault(size = 5, direction = Sort.Direction.DESC) Pageable pageable){

        Long id = user.getId();
        Page<PostGetResponseDto> postPage = postService.findPostByUser_Id(id, pageable);
        return Result.toResult(ResultCode.POST_READ_SUCCESS, postPage);

    }

    // 나누기 생성
    @PostMapping("")
    public ResponseEntity<Result> createPost(@CurrentUser User user, @Valid @RequestBody PostCreationRequestDto requestDto) {

        Long id = user.getId();
        postService.createPost(id,requestDto);
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
    public ResponseEntity<Result> updatePost(@Valid @RequestBody PostUpdateRequestDto request, @PathVariable Long id){

            postService.updatePost(id, request);
            return Result.toResult(ResultCode.POST_UPDATE_SUCCESS);
    }
}
