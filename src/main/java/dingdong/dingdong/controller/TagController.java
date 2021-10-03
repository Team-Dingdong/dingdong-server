package dingdong.dingdong.controller;

import dingdong.dingdong.dto.tag.TagRequestDto;
import dingdong.dingdong.service.tag.TagService;
import dingdong.dingdong.util.exception.Result;
import dingdong.dingdong.util.exception.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/post/tag")
public class TagController {

    private final TagService tagService;

    @PostMapping("/{id}")
    public ResponseEntity<Result> setTag(@PathVariable Long id, @RequestBody TagRequestDto request){
        tagService.addTags(id, request);
        return Result.toResult(ResultCode.TAG_UPDATE_SUCCESS);
    }

}
