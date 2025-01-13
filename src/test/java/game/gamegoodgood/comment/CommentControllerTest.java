package game.gamegoodgood.comment;

import game.gamegoodgood.config.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private CommentController commentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createComment_Success() {
        // Arrange
        Long postId = 1L;
        String token = "Bearer validToken";
        String username = "testUser";
        CommentDTO inputDto = new CommentDTO(null, "Test comment", null, null, null);
        CommentDTO savedCommentDto = new CommentDTO(1L, "Test comment", username, LocalDateTime.now(), postId);

        when(jwtTokenProvider.getUsernameFromToken(anyString())).thenReturn(username);
        when(commentService.createComment(eq(postId), eq("Test comment"), eq(username))).thenReturn(savedCommentDto);

        // Act
        ResponseEntity<CommentDTO> response = commentController.createComment(postId, inputDto, token);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(savedCommentDto.indexId(), response.getBody().indexId());
        assertEquals(savedCommentDto.detail(), response.getBody().detail());
        assertEquals(savedCommentDto.username(), response.getBody().username());
    }

    @Test
    void createComment_Unauthorized() {
        // Arrange
        Long postId = 1L;
        String token = "InvalidToken";
        CommentDTO inputDto = new CommentDTO(null, "Test comment", null, null, null);

        when(jwtTokenProvider.getUsernameFromToken(anyString())).thenThrow(new RuntimeException("Invalid token"));

        // Act
        ResponseEntity<CommentDTO> response = commentController.createComment(postId, inputDto, token);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void findAllByPostId_Success() {
        // Arrange
        Long postId = 1L;
        List<CommentDTO> commentDTOs = Arrays.asList(
                new CommentDTO(1L, "Comment 1", "user1", LocalDateTime.now(), postId),
                new CommentDTO(2L, "Comment 2", "user2", LocalDateTime.now(), postId)
        );

        when(commentService.findAllByPostId(postId)).thenReturn(commentDTOs);

        // Act
        ResponseEntity<List<CommentDTO>> response = commentController.findAllByPostId(postId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("Comment 1", response.getBody().get(0).detail());
        assertEquals("Comment 2", response.getBody().get(1).detail());
    }

    @Test
    void findAllByPostId_PostNotFound() {
        // Arrange
        Long postId = 1L;
        when(commentService.findAllByPostId(postId)).thenThrow(new RuntimeException("게시글을 찾을 수 없습니다."));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> commentController.findAllByPostId(postId));
    }
}
