package dingdong.dingdong.controller;


import dingdong.dingdong.domain.post.Post;
import dingdong.dingdong.dto.Post.PostCreationRequest;
import dingdong.dingdong.dto.Post.PostDto;
import dingdong.dingdong.service.PostService;
import dingdong.dingdong.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostApiController {

    private final PostService postService;

    // 모든 나누기 불러오기
    @GetMapping("/")
    public Page<Post> findPosts(@PageableDefault(size = 3, direction = Sort.Direction.DESC) Pageable pageable){
        return postService.findPosts(pageable);
    }
    // 몇 개씩 줄 지 상의(페이징 넣기)

    // 특정 나누기 상세보기 불러오기
    @GetMapping("/{id}")
    public PostDto findPostById(@PathVariable Long id){

        return postService.findPostById(id);
    }

    // 나누기 삭제
   @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id){
       postService.deletePost(id);
    }

    // 나누기 생성
    @PostMapping("/")
    public ResponseEntity<Post> createPost(@Valid @RequestBody PostCreationRequest request) {
        return ResponseEntity.ok(postService.createPost(request));
    }

    // 나누기 수정
    @PatchMapping("/{id}")
    public ResponseEntity<Post> updatePost(@RequestBody PostCreationRequest request,
                                           @PathVariable Long id){
        return ResponseEntity.ok(postService.updatePost(id, request));
    }
}
