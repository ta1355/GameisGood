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

    private final game.gamegoodgood.post.postSerivce postSerivce;

    private final FileUploadService fileUploadService;

    public PostController(game.gamegoodgood.post.postSerivce postSerivce, FileUploadService fileUploadService) {
        this.postSerivce = postSerivce;
        this.fileUploadService = fileUploadService;
    }

    //아이디로 찾기
    @GetMapping("/post/{id}")
    public ResponseEntity<Post> findPostById(@PathVariable Long id) {
        Post byId = postSerivce.findById(id);
        if (byId != null) {
            return ResponseEntity.ok(byId);  // 값이 있으면 200 OK
        } else {
            return ResponseEntity.notFound().build();  // 값이 없으면 404 Not Found
        }
    }

    //전체 찾기
    @GetMapping("/post")
    public ResponseEntity<List<Post>> findPostAll() {
       List<Post> posts =postSerivce.findAll();
       if (!posts.isEmpty()){
           return ResponseEntity.ok(posts);
       }else {
           return ResponseEntity.noContent().build();
       }
    }

    //게시글 만들기
    @PostMapping("/createpost")
    public ResponseEntity<Post> create(
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
        Post createPost = postSerivce.savePost(dto);

        // 저장된 게시글 위치 URI 반환
        URI location = URI.create("/posts/" + createPost.getId());

        return ResponseEntity.created(location).body(createPost);
    }

    //게시글 좋아요
    @PostMapping("/postlike/{id}")
    public ResponseEntity<Void> likePost(@PathVariable Long id) {
        postSerivce.likePost(id);  // 좋아요 처리
        return ResponseEntity.ok().build();
    }

    //게시글 삭제
    @DeleteMapping("postdelete/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id){
        postSerivce.deletedPost(id);
        return ResponseEntity.ok().build();
    }
}
