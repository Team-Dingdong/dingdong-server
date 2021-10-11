package dingdong.dingdong.controller;

import dingdong.dingdong.domain.user.CurrentUser;
import dingdong.dingdong.domain.user.User;
import dingdong.dingdong.dto.post.PostDetailResponseDto;
import dingdong.dingdong.dto.post.PostGetResponseDto;
import dingdong.dingdong.dto.post.PostRequestDto;
import dingdong.dingdong.service.chat.ChatService;
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

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/post")
public class PostController {

    private final PostService postService;
    private final ChatService chatService;

    // 홈화면, 모든 나누기 불러오기(정렬방식: 최신순)
    @GetMapping("/sorted_by=desc(createdDate)")
    public ResponseEntity<Result<Page<PostGetResponseDto>>> findPostsSortByCreatedDate(
        @CurrentUser User user,
        @PageableDefault(size = 5, direction = Sort.Direction.DESC) Pageable pageable) {

        Page<PostGetResponseDto> data;

        if (user.getLocal1() == null && user.getLocal2() == null) {
            // 유저의 local 정보가 없는 경우
            data = postService.findAllByCreateDate(pageable);
        } else {
            // 유저의 local 정보가 없는 경우(유저의 local 정보에 기반하여 나누기 GET)
            Long local1 = user.getLocal1().getId();
            Long local2 = user.getLocal2().getId();
            data = postService.findAllByCreateDateWithLocal(local1, local2, pageable);
        }
        return Result.toResult(ResultCode.POST_READ_SUCCESS, data);
    }

    // 홈화면, 모든 나누기 불러오기(정렬방식: 마감임박순)
    @GetMapping("/sorted_by=desc(endDate)")
    public ResponseEntity<Result<Page<PostGetResponseDto>>> findPostsSortByEndDate(
        @CurrentUser User user,
        @PageableDefault(size = 5, direction = Sort.Direction.DESC) Pageable pageable) {

        Page<PostGetResponseDto> data;
        if (user.getLocal1() == null && user.getLocal2() == null) {
            // 유저의 local 정보가 없는 경우
            data = postService.findAllByEndDate(pageable);
        } else {
            // 유저의 local 정보가 없는 경우(유저의 local 정보에 기반하여 나누기 GET)
            Long local1 = user.getLocal1().getId();
            Long local2 = user.getLocal2().getId();
            data = postService.findAllByEndDateWithLocal(local1, local2, pageable);
        }
        return Result.toResult(ResultCode.POST_READ_SUCCESS, data);
    }

    // 카테고리별 나누기 피드들 불러오기(카테고리 화면)(정렬 방식: 최신순)
    @GetMapping("/category/sorted_by=desc(createdDate)/{categoryId}")
    public ResponseEntity<Result<Page<PostGetResponseDto>>> findPostByCategoryIdSortByCreatedDate(
        @CurrentUser User user, @PathVariable Long categoryId,
        @PageableDefault(size = 5, direction = Sort.Direction.DESC) Pageable pageable) {

        Page<PostGetResponseDto> data;
        if (user.getLocal1() == null && user.getLocal2() == null) {
            // user가 local 정보를 설정 안 한 경우
            data = postService.findPostByCategoryId(categoryId, pageable);
        } else {
            // user가 local 정보를 설정한 경우(local 정보에 기반하여 나누기 get)
            Long local1 = user.getLocal1().getId();
            Long local2 = user.getLocal2().getId();
            data = postService.findPostByCategoryIdWithLocal(local1, local2, categoryId, pageable);
        }
        return Result.toResult(ResultCode.POST_READ_SUCCESS, data);
    }

    // 카테고리별 나누기 피드들 불러오기(카테고리 화면)(정렬 방식: 마감임박순)
    @GetMapping("/category/sorted_by=desc(endDate)/{categoryId}")
    public ResponseEntity<Result<Page<PostGetResponseDto>>> findPostByCategoryIdSortByEndDate(
        @CurrentUser User user, @PathVariable Long categoryId,
        @PageableDefault(size = 5, direction = Sort.Direction.DESC) Pageable pageable) {

        Page<PostGetResponseDto> data;
        if (user.getLocal1() == null && user.getLocal2() == null) {
            // user가 local 정보를 설정 안 한 경우
            data = postService.findPostByCategoryIdSortByEndDate(categoryId, pageable);
        } else {
            // user가 local 정보를 설정한 경우(local 정보에 기반하여 나누기 get)
            Long local1 = user.getLocal1().getId();
            Long local2 = user.getLocal2().getId();
            data = postService
                .findPostByCategoryIdSortByEndDateWithLocal(local1, local2, categoryId, pageable);
        }
        return Result.toResult(ResultCode.POST_READ_SUCCESS, data);
    }

    // 유저가 생성한 나누기 피드들 불러오기 (마이페이지 판매내역 조회)
    @GetMapping("/user/sell")
    public ResponseEntity<Result<Page<PostGetResponseDto>>> findPostByUser(@CurrentUser User user,
        @PageableDefault(size = 5, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostGetResponseDto> postPage = postService.findPostByUser(user, pageable);
        return Result.toResult(ResultCode.POST_READ_SUCCESS, postPage);
    }

    // 특정 유저(본인 제외)가 생성한 나누기 피드들 불러오기
    @GetMapping("/user/{id}")
    public ResponseEntity<Result<Page<PostGetResponseDto>>> findPostByUserId(@PathVariable Long id,
        @PageableDefault(size = 5, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostGetResponseDto> postPage = postService.findPostByUserId(id, pageable);
        return Result.toResult(ResultCode.POST_READ_SUCCESS, postPage);
    }

    // 유저가 공동구매에 참여한 나누기 피드들 불러오기 (마이페이지 구매내역 조회)
    @GetMapping("/user/buy")
    public ResponseEntity<Result<Page<PostGetResponseDto>>> findPostByUserIdOnChat(
        @CurrentUser User user,
        @PageableDefault(size = 5, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostGetResponseDto> postPage = postService.findPostByUserIdOnChatJoin(user, pageable);
        return Result.toResult(ResultCode.POST_READ_SUCCESS, postPage);
    }

    // 특정 나누기 상세보기 불러오기
    @GetMapping("/{post_id}")
    public ResponseEntity<Result<PostDetailResponseDto>> findPostById(@PathVariable Long postId) {

        PostDetailResponseDto data = postService.findPostById(postId);
        ResultCode message = ResultCode.POST_READ_SUCCESS;
        return Result.toResult(message, data);
    }

    // 나누기 생성
    @PostMapping("")
    public ResponseEntity<Result<Long>> createPost(@CurrentUser User user,
        @ModelAttribute PostRequestDto requestDto) throws IOException {
        Long postId = postService.createPost(user, requestDto);
        return Result.toResult(ResultCode.POST_CREATE_SUCCESS, postId);
    }

    // 나누기 수정
    @PatchMapping("/{id}")
    public ResponseEntity<Result> updatePost(@ModelAttribute PostRequestDto request,
        @PathVariable Long id) throws IOException {
        postService.updatePost(id, request);
        return Result.toResult(ResultCode.POST_UPDATE_SUCCESS);
    }

    // 나누기 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Result> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return Result.toResult(ResultCode.POST_DELETE_SUCCESS);
    }

    // 나누기 거래 확정 하기
    @PostMapping("/confirmed/{postId}")
    public ResponseEntity<Result> confirmed(@CurrentUser User user, @PathVariable String postId) {
        chatService.confirmedPost(user, postId);
        return Result.toResult(ResultCode.POST_CONFIRMED_SUCCESS);
    }

    // 검색 기능 구현
    // 키워드로 제목과 카테고리, 해시태그 검색
    @GetMapping("/search")
    public ResponseEntity<Result<Page<PostGetResponseDto>>> search(
        @RequestParam(value = "keyword") String keyword, @CurrentUser User user,
        @PageableDefault(size = 5, direction = Sort.Direction.DESC) Pageable pageable) {

        Page<PostGetResponseDto> data;

        if (user.getLocal1() == null && user.getLocal2() == null) {
            // user가 local 정보를 설정 안 한 경우
            data = postService.searchPosts(keyword, pageable);
        } else {
            // user가 local 정보를 설정한 경우(local 정보에 기반하여 나누기 get)
            Long local1 = user.getLocal1().getId();
            Long local2 = user.getLocal2().getId();
            data = postService.searchPostsWithLocal(keyword, local1, local2, pageable);
        }

        return Result.toResult(ResultCode.SEARCH_SUCCESS, data);
    }
}
