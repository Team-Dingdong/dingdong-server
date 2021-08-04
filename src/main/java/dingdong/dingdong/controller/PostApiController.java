package dingdong.dingdong.controller;


import dingdong.dingdong.domain.post.Post;
import dingdong.dingdong.domain.post.PostRepository;
import dingdong.dingdong.domain.user.User;
import dingdong.dingdong.domain.user.UserRepository;
import dingdong.dingdong.dto.PostCreationRequest;
import dingdong.dingdong.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostApiController {

    private final PostService postService;

    // 모든 나누기 불러오기
    @GetMapping("/all")
    public List<Post> loadPosts(){
        return postService.loadPosts();
    }
    // 몇 개씩 줄 지 상의(페이징 넣기)

    // 특정 나누기 상세보기 불러오기
    @GetMapping("/{id}")
    public Post loadPostById(@PathVariable Long id){
        Post post = postService.loadPostById(id);
        return post;
    }

    // 나누기 삭제
   @DeleteMapping("/delete/{id}")
    public void deletePost(@PathVariable Long id){
       postService.deletePost(id);
    }

    // 나누기 생성
    @PostMapping("/create")
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
