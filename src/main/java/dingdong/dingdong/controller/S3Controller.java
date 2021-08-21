package dingdong.dingdong.controller;

import dingdong.dingdong.domain.post.Post;
import dingdong.dingdong.domain.post.PostRepository;
import dingdong.dingdong.domain.user.Profile;
import dingdong.dingdong.domain.user.ProfileRepository;
import dingdong.dingdong.service.s3.S3Uploader;
import dingdong.dingdong.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static dingdong.dingdong.util.exception.ResultCode.POST_NOT_FOUND;
import static dingdong.dingdong.util.exception.ResultCode.PROFILE_NOT_FOUND;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/upload")
public class S3Controller {

    private final PostRepository postRepository;
    private final ProfileRepository profileRepository;
    private final S3Uploader s3Uploader;

    @PatchMapping("/post/{postId}")
    @ResponseBody
    public void PostUpload(@RequestParam("data") MultipartFile file, @PathVariable Long postId) throws IOException {
        String path = s3Uploader.upload(file, "static");
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND));
        log.error("이미지 업데이트 에러");

        post.setImageUrl(path);
        postRepository.save(post);
    }

    @PostMapping("/profile/{profileId}")
    @ResponseBody
    public void ProfileUpload(@RequestParam("data") MultipartFile file, @PathVariable Long profileId) throws IOException {
        String path = s3Uploader.upload(file, "static");
        Profile profile = profileRepository.findById(profileId).orElseThrow(() -> new ResourceNotFoundException(PROFILE_NOT_FOUND));

        log.error("이미지 업데이트 에러");
        profile.setProfileImageUrl(path);
        profileRepository.save(profile);
    }

}
