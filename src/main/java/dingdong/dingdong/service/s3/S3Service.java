package dingdong.dingdong.service.s3;

import dingdong.dingdong.domain.post.Post;
import dingdong.dingdong.domain.post.PostRepository;
import dingdong.dingdong.dto.s3.S3RequestDto;
import dingdong.dingdong.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static dingdong.dingdong.util.exception.ResultCode.POST_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3Service {

    private final PostRepository postRepository;
    private final S3Uploader s3Uploader;

    // 나누기에 Image 업로드
    @Transactional
    public void updatePostImage(S3RequestDto s3RequestDto, Long postId) throws IOException{

        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND));
        List<String> paths = new ArrayList<>();

        if(s3RequestDto.getPostImages() != null) {
            List<MultipartFile> files = s3RequestDto.getPostImages();
            for (MultipartFile file : files) {
                paths.add(s3Uploader.upload(file, "static"));
            }
        }

        while(paths.size() < 3){
            if(s3RequestDto.getImage_urls() != null){
                List<String> image_urls = s3RequestDto.getImage_urls();
                for(String image_url : image_urls){
                    paths.add(image_url);
                }
            }
        }

        post.setImageUrl1(paths.get(0));
        post.setImageUrl2(paths.get(1));
        post.setImageUrl3(paths.get(2));
        postRepository.save(post);
    }
}
