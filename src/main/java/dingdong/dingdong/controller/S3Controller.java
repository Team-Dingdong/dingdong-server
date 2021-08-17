package dingdong.dingdong.controller;

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

        Optional<Post> optionalPost = postRepository.findById(id);
        if(!optionalPost.isPresent()) {
            throw new ResourceNotFoundException(POST_NOT_FOUND);
        }
        Post post = optionalPost.get();

        post.setImageUrl(path);
        postRepository.save(post);
    }

    @PostMapping("/profile/image/{id}")
    @ResponseBody
    public void ProfileUpload(@RequestParam("data") MultipartFile file, @PathVariable Long id) throws IOException {
        String path = s3Uploader.upload(file, "static");

        /*
        Optional<Profile> optionalProfile = profileRepository.findById(id);
        if(!optionalProfile.isPresent()) {
            throw new ResourceNotFoundException(PROFILE_NOT_FOUND);
        }
        Profile profile = optionalProfile.get();

        profile.setProfileImageUrl()
        profileRepository.save(profile);

         */
    }

}
