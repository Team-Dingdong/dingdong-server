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
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RatingRepository ratingRepository;

    // 모든 나누기 불러오기
    @GetMapping("")
    public ResponseEntity<?> findPosts(@PageableDefault(size = 5, direction = Sort.Direction.DESC) Pageable pageable) {

        try{
            return postService.findPosts(pageable);
        } catch(RuntimeException re){
            return Result.toResult(ResultCode.VALID_ERROR);
        } catch(Exception e){
            return Result.toResult(ResultCode.INTER_SERVER_ERROR);
        }


    }

    // 특정 나누기 상세보기 불러오기
    @GetMapping("/{id}")
    public ResponseEntity<?> findPostById(@PathVariable Long id){

        try{
            return postService.findPostById(id);
        } catch(RuntimeException re){
            return Result.toResult(ResultCode.VALID_ERROR);
        } catch(Exception e){
            return Result.toResult(ResultCode.INTER_SERVER_ERROR);
        }

    }

    // 카테고리 별로 나누기 피드들 불러오기
    @GetMapping("/category/{id}")
    public ResponseEntity<?> findPostByCategory_Id(@PathVariable Long id,
                                            @PageableDefault(size = 5, direction = Sort.Direction.DESC) Pageable pageable){
        Page<PostGetResponse> postPage;

        try{
            return postService.findPostByCategory_Id(id, pageable);
        } catch(RuntimeException re){
            return Result.toResult(ResultCode.VALID_ERROR);
        } catch(Exception e){
            return Result.toResult(ResultCode.INTER_SERVER_ERROR);
        }

    }

    // 나누기 생성
    @PostMapping("")
    public ResponseEntity<?> createPost(@Valid @RequestBody PostCreationRequest request) {

        try{
            return postService.createPost(request);
        } catch(RuntimeException re){
            return Result.toResult(ResultCode.VALID_ERROR);
        }catch(Exception e){
            return Result.toResult(ResultCode.INTER_SERVER_ERROR);
        }

    }

    // 나누기 삭제
   @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id){

       try{
           return postService.deletePost(id);
       } catch(RuntimeException re){
           return Result.toResult(ResultCode.VALID_ERROR);
       }catch(Exception e){
           return Result.toResult(ResultCode.INTER_SERVER_ERROR);
       }

    }

    // 나누기 수정
    @PatchMapping("/{id}")
    public ResponseEntity<?> updatePost(@RequestBody PostUpdateRequest request,
                                           @PathVariable Long id){
        try{
            return postService.updatePost(id, request);
        } catch(RuntimeException re){
            return Result.toResult(ResultCode.VALID_ERROR);
        }catch(Exception e){
            return Result.toResult(ResultCode.INTER_SERVER_ERROR);
        }
    }
}
