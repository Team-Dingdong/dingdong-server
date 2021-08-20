package dingdong.dingdong.controller;

import dingdong.dingdong.domain.user.Profile;
import dingdong.dingdong.service.s3.S3Uploader;
import dingdong.dingdong.domain.post.Post;
import dingdong.dingdong.domain.post.PostRepository;
import dingdong.dingdong.domain.user.ProfileRepository;
import dingdong.dingdong.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static dingdong.dingdong.util.exception.ResultCode.POST_NOT_FOUND;
import static dingdong.dingdong.util.exception.ResultCode.PROFILE_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@Controller
public class S3Controller {

    private final PostRepository postRepository;
    private final ProfileRepository profileRepository;
    private final S3Uploader s3Uploader;

    @PatchMapping("/post/image/{id}")
    @ResponseBody
    public void PostUpload(@RequestParam("data") MultipartFile file, @PathVariable Long id) throws IOException {
        String path = s3Uploader.upload(file, "static");

        log.error("이미지 업데이트 에러");

        Post optionalPost = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND));
        Post post = optionalPost;

        post.setImageUrl(path);
        postRepository.save(post);
    }

    @PatchMapping("/profile/image/{id}")
    @ResponseBody
    public void ProfileUpload(@RequestParam("data") MultipartFile file, @PathVariable Long id) throws IOException {
        String path = s3Uploader.upload(file, "static");

        log.error("이미지 업데이트 에러");

        Profile optionalProfile = profileRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(PROFILE_NOT_FOUND));
        Profile profile = optionalProfile;

        profile.setProfileImageUrl(path);
        profileRepository.save(profile);
    }

}
