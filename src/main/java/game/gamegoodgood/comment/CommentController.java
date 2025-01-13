package game.gamegoodgood.comment;

import game.gamegoodgood.config.jwt.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CommentController {
    private final CommentService commentService;
    private final JwtTokenProvider jwtTokenProvider;

    public CommentController(CommentService commentService, JwtTokenProvider jwtTokenProvider) {
        this.commentService = commentService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    //댓글 작성
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentDTO> createComment(@PathVariable Long postId, @RequestBody CommentDTO dto,
                                                    @RequestHeader("Authorization") String token) {
        String username = extractUsernameFromToken(token);
        if (username == null) {
            return ResponseEntity.status(401).build();
        }
        CommentDTO createdComment = commentService.createComment(postId, dto.detail(), username);
        return ResponseEntity.ok(createdComment);
    }


    // 게시글에 대한 댓글 조회
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentDTO>> findAllByPostId(@PathVariable Long postId) {
        List<CommentDTO> comments = commentService.findAllByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    //댓글 삭제(하드)
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId, @RequestHeader("Authorization") String token){
        String username = extractUsernameFromToken(token);

        if (username ==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증되지 않은 사용자입니다.");
        }

        try {
            commentService.deleteComment(commentId,username);
            return ResponseEntity.ok("댓글이 성공적으로 삭제되었습니다.");

        }catch (RuntimeException e){
            return  ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    // 토큰검증
    private String extractUsernameFromToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                return jwtTokenProvider.getUsernameFromToken(token);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}

