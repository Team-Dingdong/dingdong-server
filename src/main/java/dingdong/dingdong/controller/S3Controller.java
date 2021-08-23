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

    @PatchMapping("/post/{postId}/{imageId}")
    @ResponseBody
    public ResponseEntity<Result> PostUpload(@RequestParam("data") MultipartFile file, @PathVariable Long postId, @PathVariable Long imageId) throws IOException {

        String path = s3Uploader.upload(file, "static");
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND));
        //log.error("이미지 업데이트 에러");

        if(imageId == 1){
            String filePath = post.getImageUrl1();
            s3Uploader.deleteObject(filePath);
            post.setImageUrl1(path);
        } else if(imageId == 2){
            String filePath = post.getImageUrl2();
            s3Uploader.deleteObject(filePath);
            post.setImageUrl2(path);
        } else if(imageId == 3){
            String filePath = post.getImageUrl3();
            s3Uploader.deleteObject(filePath);
            post.setImageUrl3(path);
        }else{
            return Result.toResult(ResultCode.POSTNUMBER_NOT_FOUND);
        }

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
