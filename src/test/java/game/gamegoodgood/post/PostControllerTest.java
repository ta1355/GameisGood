package game.gamegoodgood.post;

import game.gamegoodgood.config.jwt.JwtAuthenticationFilter;
import game.gamegoodgood.config.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class PostControllerTest {

    @InjectMocks
    private PostController postController;

    @Mock
    private PostService postService;

    @Mock
    private FileUploadService fileUploadService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindPostById() {
        Long postId = 1L;
        PostWithUserDto mockPost = new PostWithUserDto(1L, "Test Title", "Test Detail", "Test Game", "image.jpg", "testUser", null, null, 0, 0);
        when(postService.findById(postId)).thenReturn(mockPost);

        ResponseEntity<PostWithUserDto> response = postController.findPostById(postId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockPost, response.getBody());
    }

    @Test
    void testFindPostById_NotFound() {
        Long postId = 1L;
        when(postService.findById(postId)).thenReturn(null);

        ResponseEntity<PostWithUserDto> response = postController.findPostById(postId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testFindPostAll() {
        int page = 0;
        int size = 10;
        List<PostWithUserDto> posts = Arrays.asList(
                new PostWithUserDto(1L, "Title1", "Detail1", "Game1", "image1.jpg", "user1", null, null, 0, 0),
                new PostWithUserDto(2L, "Title2", "Detail2", "Game2", "image2.jpg", "user2", null, null, 0, 0)
        );
        Page<PostWithUserDto> pageResult = new PageImpl<>(posts);
        when(postService.findAll(any(PageRequest.class))).thenReturn(pageResult);

        ResponseEntity<Page<PostWithUserDto>> response = postController.findPostAll(page, size);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pageResult, response.getBody());
    }

    @Test
    void testCreatePost() throws Exception {
        String title = "Test Title";
        String detail = "Test Detail";
        String game = "Test Game";
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test image content".getBytes());

        when(fileUploadService.saveImage(any())).thenReturn("path/to/image.jpg");
        PostWithUserDto savedPost = new PostWithUserDto(1L, title, detail, game, "path/to/image.jpg", "testUser", null, null, 0, 0);
        when(postService.savePost(any(PostDTO.class))).thenReturn(savedPost);

        ResponseEntity<PostWithUserDto> response = postController.create(title, detail, game, image);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(savedPost, response.getBody());
    }

    @Test
    void testLikePost() {
        Long postId = 1L;
        doNothing().when(postService).likePost(postId);

        ResponseEntity<Void> response = postController.likePost(postId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(postService, times(1)).likePost(postId);
    }

    @Test
    void testDeletePost_Authorized() {
        Long postId = 1L;
        String username = "testUser";
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(username);
        doNothing().when(postService).deletedPost(postId, username);

        ResponseEntity<Void> response = postController.deletePost(postId, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(postService, times(1)).deletedPost(postId, username);
    }

    @Test
    void testDeletePost_Unauthorized() {
        Long postId = 1L;
        when(authentication.isAuthenticated()).thenReturn(false);

        ResponseEntity<Void> response = postController.deletePost(postId, authentication);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testIncrementViewCount() {
        Long postId = 1L;
        doNothing().when(postService).incrementViewCount(postId);

        ResponseEntity<Void> response = postController.incrementViewCount(postId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(postService, times(1)).incrementViewCount(postId);
    }

    @Test
    void testGetTodayPopularPosts() {
        List<PostTodayPopularityDTO> popularPosts = Arrays.asList(
                new PostTodayPopularityDTO(1L, "Title1", "User a",LocalDateTime.now(),0),
                new PostTodayPopularityDTO(2L, "Title2", "User b", LocalDateTime.now(),0)
        );
        when(postService.getTodayTopViewedPosts()).thenReturn(popularPosts);

        ResponseEntity<List<PostTodayPopularityDTO>> response = postController.getTodayPopularPosts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(popularPosts, response.getBody());
    }
}