package dingdong.dingdong.controller;

import dingdong.dingdong.domain.user.CurrentUser;
import dingdong.dingdong.domain.user.User;
import dingdong.dingdong.dto.post.*;
import dingdong.dingdong.service.chat.ChatService;
import dingdong.dingdong.service.post.PostService;
import dingdong.dingdong.util.exception.Result;
import dingdong.dingdong.util.exception.ResultCode;
import java.util.List;
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
@RequestMapping("/api/v1/post")
@RestController
public class PostController {

    private final PostService postService;
    private final ChatService chatService;

    // 홈화면, 모든 나누기 불러오기(정렬방식: 최신순)(유저의 local 정보 기반)
    @GetMapping("/sort=desc&sortby=createdDate&local/{localId}")
    public ResponseEntity<Result<Page<PostGetResponseDto>>> findPostsSortByCreatedDate(
        @CurrentUser User user, @PathVariable Long localId,
        @PageableDefault(size = 5) Pageable pageable) {
        Page<PostGetResponseDto> data = postService.findAllByCreateDateWithLocal(user, localId, pageable);

        return Result.toResult(ResultCode.POST_READ_SUCCESS, data);
    }

    // 홈화면, 모든 나누기 불러오기(정렬방식: 마감임박순)(유저의 local 정보 기반)
    @GetMapping("/sort=desc&sortby=endDate&local/{localId}")
    public ResponseEntity<Result<Page<PostGetResponseDto>>> findPostsSortByEndDate(
        @CurrentUser User user, @PathVariable Long localId,
        @PageableDefault(size = 5, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostGetResponseDto> data = postService.findAllByEndDateWithLocal(user, localId, pageable);

        return Result.toResult(ResultCode.POST_READ_SUCCESS, data);
    }

    // 카테고리별 나누기 피드들 불러오기(카테고리 화면)(정렬 방식: 최신순)(유저의 local 정보 기반)
    @GetMapping("/sort=desc&sortby=category&createdDate&local/{categoryId}/{localId}")
    public ResponseEntity<Result<Page<PostGetResponseDto>>> findPostByCategoryIdSortByCreatedDate(
        @CurrentUser User user, @PathVariable("categoryId") Long categoryId, @PathVariable("localId") Long localId,
        @PageableDefault(size = 5) Pageable pageable) {
        Page<PostGetResponseDto> data = postService.
            findPostByCategoryIdWithLocal(user, categoryId, localId, pageable);

        return Result.toResult(ResultCode.POST_READ_SUCCESS, data);
    }

    // 카테고리별 나누기 피드들 불러오기(카테고리 화면)(정렬 방식: 마감임박순)(유저의 local 정보 기반)
    @GetMapping("/sort=desc&sortby=category&endDate&local/{categoryId}/{localId}")
    public ResponseEntity<Result<Page<PostGetResponseDto>>> findPostByCategoryIdSortByEndDate(
        @CurrentUser User user, @PathVariable("categoryId") Long categoryId, @PathVariable("localId") Long localId,
        @PageableDefault(size = 5, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostGetResponseDto> data = postService
                .findPostByCategoryIdSortByEndDateWithLocal(user, categoryId, localId, pageable);

        return Result.toResult(ResultCode.POST_READ_SUCCESS, data);
    }

    // 유저가 생성한 나누기 피드들 불러오기 (마이페이지 판매내역 조회)
    @GetMapping("/user/sell")
    public ResponseEntity<Result<List<PostGetResponseDto>>> findPostByUser(@CurrentUser User user){
        List<PostGetResponseDto> data = postService.findPostByUser(user);
        return Result.toResult(ResultCode.POST_READ_SUCCESS, data);
    }

    // 특정 유저(본인 제외)가 생성한 나누기 피드들 불러오기
    @GetMapping("/user/{userId}")
    public ResponseEntity<Result<Page<PostGetResponseDto>>> findPostByUserId(
        @PathVariable Long userId,
        @PageableDefault(size = 5, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostGetResponseDto> data = postService.findPostByUserId(userId, pageable);
        return Result.toResult(ResultCode.POST_READ_SUCCESS, data);
    }

    // 유저가 공동구매에 참여한 나누기 피드들 불러오기 (마이페이지 구매내역 조회)
    @GetMapping("/user/buy")
    public ResponseEntity<Result<List<PostGetResponseDto>>> findPostByUserIdOnChat(@CurrentUser User user) {
        List<PostGetResponseDto> data = postService.findPostByUserIdOnChatJoin(user);
        return Result.toResult(ResultCode.POST_READ_SUCCESS, data);
    }

    // 특정 나누기 상세보기 불러오기
    @GetMapping("/{postId}")
    public ResponseEntity<Result<PostDetailResponseDto>> findPostById(@PathVariable Long postId) {
        PostDetailResponseDto data = postService.findPostById(postId);
        return Result.toResult(ResultCode.POST_READ_SUCCESS, data);
    }

    // 나누기 생성
    @PostMapping("/{localId}")
    public ResponseEntity<Result<PostResponseDto>> createPost(@CurrentUser User user, @PathVariable Long localId,
        @ModelAttribute @Valid PostCreateRequestDto postCreateRequestDto) {
        log.info("postcontroller create ===========================");
        Long postId = postService.createPost(user, localId, postCreateRequestDto);
        PostResponseDto data = PostResponseDto.builder()
            .id(postId)
            .build();
        return Result.toResult(ResultCode.POST_CREATE_SUCCESS, data);
    }

    // 나누기 수정
    @PostMapping("/edit/{postId}")
    public ResponseEntity<Result<PostResponseDto>> updatePost(@PathVariable Long postId,
        @ModelAttribute @Valid PostUpdateRequestDto postUpdateRequestDto) {
        postService.updatePost(postId, postUpdateRequestDto);
        PostResponseDto data = PostResponseDto.builder()
            .id(postId)
            .build();
        return Result.toResult(ResultCode.POST_UPDATE_SUCCESS, data);
    }

    // 나누기 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<Result> deletePost(@CurrentUser User user, @PathVariable Long postId) {
        postService.deletePost(user, postId);
        return Result.toResult(ResultCode.POST_DELETE_SUCCESS);
    }

    // 나누기 거래 확정 하기
    @PostMapping("/confirmed/{postId}")
    public ResponseEntity<Result> confirmed(@CurrentUser User user, @PathVariable Long postId) {
        chatService.confirmedPost(user, postId);
        return Result.toResult(ResultCode.POST_CONFIRMED_SUCCESS);
    }

    // 검색 기능 구현
    // 키워드로 제목과 카테고리, 해시태그 검색
    @GetMapping("/search")
    public ResponseEntity<Result<Page<PostGetResponseDto>>> search(
        @RequestParam(value = "keyword") String keyword, @CurrentUser User user,
        @PageableDefault(size = 5, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostGetResponseDto> data = postService.searchPostsWithLocal(keyword, user, pageable);
        return Result.toResult(ResultCode.SEARCH_SUCCESS, data);
    }

    // 홈화면, 모든 나누기 불러오기(정렬방식: 최신순)(local 정보를 무시)
    @GetMapping("/sort=desc&sortby=createdDate")
    public ResponseEntity<Result<Page<PostGetResponseDto>>> findPostsSortByCreatedDateNotLocal(
        @PageableDefault(size = 5) Pageable pageable) {
        Page<PostGetResponseDto> data = postService.findPostsSortByCreatedDateNotLocal(pageable);

        return Result.toResult(ResultCode.POST_READ_SUCCESS, data);
    }

    // 홈화면, 모든 나누기 불러오기(정렬방식: 마감임박순)(local 정보를 무시)
    @GetMapping("/sort=desc&sortby=endDate")
    public ResponseEntity<Result<Page<PostGetResponseDto>>> findPostsSortByEndDateNotLocal(
        @PageableDefault(size = 5, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostGetResponseDto> data = postService.findPostsSortByEndDateNotLocal(pageable);

        return Result.toResult(ResultCode.POST_READ_SUCCESS, data);
    }

    // 카테고리별 나누기 피드들 불러오기(카테고리 화면)(정렬 방식: 최신순)(local 정보를 무시)
    @GetMapping("/sort=desc&sortby=category&createdDate/{categoryId}")
    public ResponseEntity<Result<Page<PostGetResponseDto>>> findPostByCategoryIdSortByCreatedDateNotLocal(
        @CurrentUser User user, @PathVariable Long categoryId,
        @PageableDefault(size = 5) Pageable pageable) {
        Page<PostGetResponseDto> data = postService.
            findPostByCategoryIdSortByCreatedDateNotLocal(categoryId, pageable);

        return Result.toResult(ResultCode.POST_READ_SUCCESS, data);
    }

    // 카테고리별 나누기 피드들 불러오기(카테고리 화면)(정렬 방식: 마감임박순)(local 정보를 무시)
    @GetMapping("/sort=desc&sortby=category&endDate/{categoryId}")
    public ResponseEntity<Result<Page<PostGetResponseDto>>> findPostByCategoryIdSortByEndDateNotLocal(
        @CurrentUser User user, @PathVariable Long categoryId,
        @PageableDefault(size = 5, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostGetResponseDto> data = postService
            .findPostByCategoryIdSortByEndDateNotLocal(categoryId, pageable);

        return Result.toResult(ResultCode.POST_READ_SUCCESS, data);
    }
}
