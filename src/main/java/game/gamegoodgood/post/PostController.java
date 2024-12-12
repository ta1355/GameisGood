package game.gamegoodgood.post;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
public class PostController {

    private final postService postService;
    private final FileUploadService fileUploadService;

    public PostController(postService postService, FileUploadService fileUploadService) {
        this.postService = postService;
        this.fileUploadService = fileUploadService;
    }

    // 아이디로 찾기 (PostWithUserDto 반환)
    @GetMapping("/post/{id}")
    public ResponseEntity<PostWithUserDto> findPostById(@PathVariable Long id) {
        PostWithUserDto postDto = postService.findById(id);
        if (postDto != null) {
            return ResponseEntity.ok(postDto);  // 값이 있으면 200 OK
        } else {
            return ResponseEntity.notFound().build();  // 값이 없으면 404 Not Found
        }
    }

    // 전체 찾기 (PostWithUserDto 리스트 반환)
    @GetMapping("/post")
    public ResponseEntity<List<PostWithUserDto>> findPostAll() {
        List<PostWithUserDto> posts = postService.findAll();
        if (!posts.isEmpty()) {
            return ResponseEntity.ok(posts);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    // 게시글 만들기 (PostWithUserDto 반환)
    @PostMapping("/createpost")
    public ResponseEntity<PostWithUserDto> create(
            @RequestParam("title") String title,
            @RequestParam("detail") String detail,
            @RequestParam("game") String game,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        String imagePath = null;

        // 이미지 파일이 존재한다면, 저장 후 경로 반환
        if (image != null) {
            try {
                imagePath = fileUploadService.saveImage(image);  // 이미지 파일을 서버에 저장
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }

        // DTO 생성 (이미지 경로 포함)
        PostDto dto = new PostDto(title, detail, game, imagePath);

        // 게시글 저장
        PostWithUserDto createPost = postService.savePost(dto);

        // 저장된 게시글 위치 URI 반환
        URI location = URI.create("/posts/" + createPost.id());

        return ResponseEntity.created(location).body(createPost);
    }

    // 게시글 좋아요
    @PostMapping("/postlike/{id}")
    public ResponseEntity<Void> likePost(@PathVariable Long id) {
        postService.likePost(id);  // 좋아요 처리
        return ResponseEntity.ok().build();
    }

    // 게시글 삭제
    @DeleteMapping("postdelete/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id){
        postService.deletedPost(id);
        return ResponseEntity.ok().build();
    }
}
