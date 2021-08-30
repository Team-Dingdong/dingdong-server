package dingdong.dingdong.controller;

import dingdong.dingdong.domain.post.Post;
import dingdong.dingdong.domain.post.PostRepository;
import dingdong.dingdong.domain.user.Profile;
import dingdong.dingdong.domain.user.ProfileRepository;
import dingdong.dingdong.service.s3.S3Uploader;
import dingdong.dingdong.util.exception.ResourceNotFoundException;
import dingdong.dingdong.util.exception.Result;
import dingdong.dingdong.util.exception.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    public ResponseEntity<Result> PostUpload(@RequestParam("files") List<MultipartFile> files, @PathVariable Long postId) throws IOException {

        List<String> paths = new ArrayList<>();
        for (MultipartFile file : files){
            paths.add(s3Uploader.upload(file, "static"));
        }
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND));

        while(paths.size() < 3){
            paths.add("https://dingdongbucket.s3.ap-northeast-2.amazonaws.com/static/default_post.png");
        }

        post.setImageUrl1(paths.get(0));
        post.setImageUrl2(paths.get(1));
        post.setImageUrl3(paths.get(2));
        postRepository.save(post);
        return Result.toResult(ResultCode.IMAGE_UPLOAD_SUCCESS);
    }

    @PatchMapping("/profile/{profileId}")
    @ResponseBody
    public ResponseEntity<Result> ProfileUpload(@RequestParam("data") MultipartFile file, @PathVariable Long profileId) throws IOException {
        String path = s3Uploader.upload(file, "static");
        Profile profile = profileRepository.findById(profileId).orElseThrow(() -> new ResourceNotFoundException(PROFILE_NOT_FOUND));

        log.error("이미지 업데이트 에러");
        profile.setProfileImageUrl(path);
        profileRepository.save(profile);
        return Result.toResult(ResultCode.IMAGE_UPLOAD_SUCCESS);
    }

}
