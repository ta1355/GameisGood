package game.gamegoodgood.post;

import game.gamegoodgood.config.jwt.JwtAuthenticationFilter;
import game.gamegoodgood.config.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
public class PostController {

    private final PostService postService;
    private final FileUploadService fileUploadService;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public PostController(PostService postService, FileUploadService fileUploadService, JwtTokenProvider jwtTokenProvider, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.postService = postService;
        this.fileUploadService = fileUploadService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // 아이디로 찾기
    @GetMapping("/post/{id}")
    public ResponseEntity<PostWithUserDto> findPostById(@PathVariable Long id) {
        PostWithUserDto postDto = postService.findById(id);
        if (postDto != null) {
            return ResponseEntity.ok(postDto);  // 값이 있으면 200 OK
        } else {
            return ResponseEntity.notFound().build();  // 값이 없으면 404 Not Found
        }
    }

    // 전체 찾기
    @GetMapping("/post")
    public ResponseEntity<Page<PostWithUserDto>> findPostAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PostWithUserDto> posts = postService.findAll(pageable);
        return ResponseEntity.ok(posts);
    }

    // 게시글 만들기
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
        PostDTO dto = new PostDTO(title, detail, game, imagePath);

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
    public ResponseEntity<Void> deletePost(@PathVariable Long id, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            try {
                postService.deletedPost(id, username);
                return ResponseEntity.ok().build();
            } catch (PostExceptions.PostNotFoundException e) {
                return ResponseEntity.notFound().build();
            } catch (PostExceptions.UnauthorizedException e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    // 조회수 증가
    @PostMapping("/post/{id}/view")
    public ResponseEntity<Void> incrementViewCount(@PathVariable Long id) {
        postService.incrementViewCount(id);
        return ResponseEntity.ok().build();
    }

    //조회수 높은 순
    @GetMapping("/post/today/popularity")
    public ResponseEntity<List<PostTodayPopularityDTO>> getTodayPopularPosts() {
        List<PostTodayPopularityDTO> popularPosts = postService.getTodayTopViewedPosts();
        return ResponseEntity.ok(popularPosts);
    }

}
