package dingdong.dingdong.controller;

import dingdong.dingdong.domain.post.Post;
import dingdong.dingdong.domain.post.PostRepository;
import dingdong.dingdong.domain.user.Profile;
import dingdong.dingdong.domain.user.ProfileRepository;
import dingdong.dingdong.dto.s3.S3RequestDto;
import dingdong.dingdong.service.s3.S3Service;
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

    private final ProfileRepository profileRepository;
    private final S3Uploader s3Uploader;
    private final S3Service s3Service;

    // post 생성, 수정시 이미지 파일 서버 전송 후, URL을 DB에 저장
    @PatchMapping("/post/{postId}")
    public ResponseEntity<Result> PostUpload(@ModelAttribute S3RequestDto s3RequestDto, @PathVariable Long postId) throws IOException {
        s3Service.updatePostImage(s3RequestDto, postId);
        return Result.toResult(ResultCode.IMAGE_UPLOAD_SUCCESS);
    }

    // profile 생성, 수정시 이미지 파일 서버 전송 후, URL을 DB에 저장
    @PatchMapping("/profile/{profileId}")
    @ResponseBody
    public ResponseEntity<Result> ProfileUpload(@RequestParam("data") MultipartFile file, @PathVariable Long profileId) throws IOException {
        String path = s3Uploader.upload(file, "static");
        Profile profile = profileRepository.findById(profileId).orElseThrow(() -> new ResourceNotFoundException(PROFILE_NOT_FOUND));

        profile.setProfileImageUrl(path);
        profileRepository.save(profile);
        return Result.toResult(ResultCode.IMAGE_UPLOAD_SUCCESS);
    }

}
