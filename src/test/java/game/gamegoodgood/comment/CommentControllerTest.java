package game.gamegoodgood.comment;

import game.gamegoodgood.comment.Comment;
import game.gamegoodgood.comment.CommentDTO;
import game.gamegoodgood.comment.CommentService;
import game.gamegoodgood.config.jwt.JwtTokenProvider;
import game.gamegoodgood.post.Post;
import game.gamegoodgood.post.PostRepository;
import game.gamegoodgood.user.UserRepository;
import game.gamegoodgood.user.Users;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

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

        Post post = new Post();
        post.setId(postId);

        Users user = new Users();
        user.setUsername(username);

        Comment savedComment = new Comment();
        savedComment.setIndexId(1L);
        savedComment.setDetail("Test comment");
        savedComment.setUsers(user);
        savedComment.setPost(post);
        savedComment.setCreatedDateTime(LocalDateTime.now());

        when(jwtTokenProvider.getUsernameFromToken(anyString())).thenReturn(username);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(commentService.createComment(any(Post.class), anyString(), any(Users.class))).thenReturn(savedComment);

        // Act
        ResponseEntity<CommentDTO> response = commentController.createComment(postId, inputDto, token);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(savedComment.getIndexId(), response.getBody().indexId());
        assertEquals(savedComment.getDetail(), response.getBody().detail());
        assertEquals(username, response.getBody().username());
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
        Post post = new Post();
        post.setId(postId);

        Comment comment1 = new Comment();
        comment1.setIndexId(1L);
        comment1.setDetail("Comment 1");
        comment1.setUsers(new Users());
        comment1.getUsers().setUsername("user1");
        comment1.setPost(post);
        comment1.setCreatedDateTime(LocalDateTime.now());

        Comment comment2 = new Comment();
        comment2.setIndexId(2L);
        comment2.setDetail("Comment 2");
        comment2.setUsers(new Users());
        comment2.getUsers().setUsername("user2");
        comment2.setPost(post);
        comment2.setCreatedDateTime(LocalDateTime.now());

        List<Comment> comments = Arrays.asList(comment1, comment2);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentService.findAllByPost(post)).thenReturn(comments);

        // Act
        ResponseEntity<List<CommentDTO>> response = commentController.findAllByPostId(postId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(comment1.getDetail(), response.getBody().get(0).detail());
        assertEquals(comment2.getDetail(), response.getBody().get(1).detail());
    }

    @Test
    void findAllByPostId_PostNotFound() {
        // Arrange
        Long postId = 1L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> commentController.findAllByPostId(postId));
    }
}