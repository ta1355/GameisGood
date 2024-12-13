package game.gamegoodgood.comment;

import game.gamegoodgood.config.auth.JwtTokenProvider;
import game.gamegoodgood.post.Post;
import game.gamegoodgood.post.PostRepository;
import game.gamegoodgood.user.UserRepository;
import game.gamegoodgood.user.Users;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class CommentController {

    private final CommentService commentService;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider; // JwtTokenProvider 주입

    public CommentController(CommentService commentService, PostRepository postRepository,
                             UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.commentService = commentService;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 댓글 생성
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentDto> createComment(@PathVariable Long postId, @RequestBody CommentDto dto,
                                                    @RequestHeader("Authorization") String token) {
        // JWT 토큰에서 사용자 정보 추출
        String username = extractUsernameFromToken(token);

        if (username == null) {
            return ResponseEntity.status(401).build(); // 인증되지 않은 사용자 처리
        }

        // 해당 postId에 맞는 Post 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        // JWT 토큰에서 인증된 사용자 정보로 유저 조회
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 댓글 생성 및 저장
        Comment comment = commentService.createComment(post, dto.detail(), user);

        // CommentDto로 변환하여 응답
        CommentDto responseDto = new CommentDto(
                comment.getIndexId(),
                comment.getDetail(),
                comment.getUsers().getUsername(),
                comment.getCreatedDateTime(),
                comment.getPost().getId()
        );

        return ResponseEntity.ok(responseDto); // 성공적으로 댓글 생성
    }

    // 댓글 목록 조회 (특정 게시글에 대한 모든 댓글 조회)
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentDto>> findAllByPostId(@PathVariable Long postId) {
        // 해당 postId에 맞는 Post 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        // 해당 게시글에 대한 모든 댓글을 조회
        List<Comment> comments = commentService.findAllByPost(post);

        // 댓글 목록을 CommentDto 목록으로 변환
        List<CommentDto> commentDtos = comments.stream()
                .map(comment -> new CommentDto(
                        comment.getIndexId(),
                        comment.getDetail(),
                        comment.getUsers().getUsername(),
                        comment.getCreatedDateTime(),
                        comment.getPost().getId()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(commentDtos); // 댓글 목록 반환
    }

    // JWT 토큰에서 사용자 이름 추출 (토큰에서 "Bearer " 접두어 처리)
    private String extractUsernameFromToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // "Bearer " 접두어를 제거한 토큰만 추출
            try {
                return jwtTokenProvider.getUsernameFromToken(token); // JWT 토큰에서 사용자 이름 추출
            } catch (Exception e) {
                // 예외 발생 시 null 반환
                return null;
            }
        }
        return null; // "Bearer "가 포함되지 않은 토큰은 처리하지 않음
    }
}
