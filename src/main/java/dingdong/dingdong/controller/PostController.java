package dingdong.dingdong.controller;

import dingdong.dingdong.domain.user.CurrentUser;
import dingdong.dingdong.domain.user.User;
import dingdong.dingdong.dto.post.PostRequestDto;
import dingdong.dingdong.dto.post.PostDetailResponseDto;
import dingdong.dingdong.dto.post.PostGetResponseDto;

import dingdong.dingdong.service.post.PostService;
import dingdong.dingdong.util.exception.Result;
import dingdong.dingdong.util.exception.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/post")
public class PostController {

    private final PostService postService;

    // 모든 나누기 불러오기(최신순으로)
    @GetMapping("/sorted_by=desc(createdDate)")
    public ResponseEntity<Result<Page<PostGetResponseDto>>> findPostsSortByCreatedDate(@CurrentUser User user, @PageableDefault(size = 5, direction = Sort.Direction.DESC) Pageable pageable) {

        Long local1 = user.getLocal1().getId();
        Long local2 = user.getLocal2().getId();
        log.error("전체 나누기 불러오기 에러");
        Page<PostGetResponseDto> data = postService.findAllByCreateDate(local1, local2, pageable);
        return Result.toResult(ResultCode.POST_READ_SUCCESS, data);

    }

    // 모든 나누기 불러오기(마감임박순으로)
    @GetMapping("/sorted_by=desc(endDate)")
    public ResponseEntity<Result<Page<PostGetResponseDto>>> findPostsSortByEndDate(@CurrentUser User user, @PageableDefault(size = 5, direction = Sort.Direction.DESC) Pageable pageable) {

        Long local1 = user.getLocal1().getId();
        Long local2 = user.getLocal2().getId();
        log.error("전체 나누기 불러오기 에러");
        Page<PostGetResponseDto> data = postService.findAllByEndDate(local1, local2, pageable);
        return Result.toResult(ResultCode.POST_READ_SUCCESS, data);

    }

    // 특정 나누기 상세보기 불러오기
    @GetMapping("/{post_id}")
    public ResponseEntity<Result<PostDetailResponseDto>> findPostById(@PathVariable Long post_id) {

        PostDetailResponseDto data = postService.findPostById(post_id);
        log.error("나누기 상세보기 에러");
        ResultCode message = ResultCode.POST_READ_SUCCESS;
        return Result.toResult(message, data);
    }

    // 카테고리 별로 나누기 피드들 불러오기
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Result<Page<PostGetResponseDto>>> findPostByCategoryId(@CurrentUser User user, @PathVariable Long categoryId, @PageableDefault(size = 5, direction = Sort.Direction.DESC) Pageable pageable){
        Long local1 = user.getLocal1().getId();
        Long local2 = user.getLocal2().getId();
        log.error("카테고리 별로 나누기 피드들 불러오기 에러");
        Page<PostGetResponseDto> data = postService.findPostByCategoryId(local1, local2, categoryId, pageable);
        return Result.toResult(ResultCode.POST_READ_SUCCESS, data);
    }

    // 유저 별로 나누기 피드들 불러오기
    @GetMapping("/user")
    public ResponseEntity<Result<Page<PostGetResponseDto>>> findPostByUserId(@CurrentUser User user, @PageableDefault(size = 5, direction = Sort.Direction.DESC) Pageable pageable){
        Page<PostGetResponseDto> postPage = postService.findPostByUserId(user, pageable);
        log.error("유저 별로 나누기 피드들 불러오기 에러");
        return Result.toResult(ResultCode.POST_READ_SUCCESS, postPage);

    }

    // 나누기 생성
    @PostMapping("")
    public ResponseEntity<Result<Long>> createPost(@CurrentUser User user, @Valid @RequestBody PostRequestDto requestDto) {

        Long postId = postService.createPost(user, requestDto);
        log.error("나누기 생성 에러");
        return Result.toResult(ResultCode.POST_CREATE_SUCCESS, postId );

    }

    // 나누기 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Result> deletePost(@PathVariable Long id){

       postService.deletePost(id);
        log.error("나누기 삭제 에러");
       return Result.toResult(ResultCode.POST_DELETE_SUCCESS);

    }

    // 나누기 수정
    @PatchMapping("/{id}")
    public ResponseEntity<Result> updatePost(@Valid @RequestBody PostRequestDto request, @PathVariable Long id){
        postService.updatePost(id, request);
        log.error("나누기 수정 에러");
        return Result.toResult(ResultCode.POST_UPDATE_SUCCESS);
    }

    // 검색 기능 구현
    //키워드로 제목과 카테고리 검색
   @GetMapping("/search")
    public ResponseEntity<Result<Page<PostGetResponseDto>>> search(@RequestParam(value = "keyword") String keyword, @CurrentUser User user, @PageableDefault(size = 5, direction = Sort.Direction.DESC) Pageable pageable) {

        Long local1 = user.getLocal1().getId();
        Long local2 = user.getLocal2().getId();
        Page<PostGetResponseDto> data = postService.searchPosts(keyword, local1, local2, pageable);
        return Result.toResult(ResultCode.SEARCH_SUCCESS, data);
    }

}
