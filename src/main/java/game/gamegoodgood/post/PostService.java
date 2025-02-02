package game.gamegoodgood.post;

import game.gamegoodgood.post.like.Like;
import game.gamegoodgood.post.like.LikeRepository;
import game.gamegoodgood.user.UserRepository;
import game.gamegoodgood.user.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private static final Logger log = LoggerFactory.getLogger(PostService.class);
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository, LikeRepository likeRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
    }

    public PostWithUserDto savePost(PostDTO dto) {
        String image = dto.image();
        if (image == null || image.isEmpty()) {
            image = null;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("로그인 정보가 없습니다.");
        }
        String username = authentication.getName();

        Users currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Post post = new Post(dto.title(), dto.detail(), dto.game(), image);
        post.setUsers(currentUser);

        Post savedPost = postRepository.save(post);

        return new PostWithUserDto(
                savedPost.getId(),
                savedPost.getTitle(),
                savedPost.getDetail(),
                savedPost.getGame(),
                savedPost.getImage(),
                currentUser.getUsername(),
                savedPost.getCreateDateTime(),
                savedPost.getDeletedDateTime(),
                savedPost.getLikeCount(),
                savedPost.getViewCount()
        );
    }

    public PostWithUserDto findById(Long id) {
        Post post = postRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        Users user = post.getUsers();

        return new PostWithUserDto(
                post.getId(),
                post.getTitle(),
                post.getDetail(),
                post.getGame(),
                post.getImage(),
                user.getUsername(),
                post.getCreateDateTime(),
                post.getDeletedDateTime(),
                post.getLikeCount(),
                post.getViewCount()
        );
    }

    public Page<PostWithUserDto> findAll(Pageable pageable) {
        Page<Post> postPage = postRepository.findAllByDeletedFalse(pageable);
        return postPage.map(post -> new PostWithUserDto(
                post.getId(),
                post.getTitle(),
                post.getDetail(),
                post.getGame(),
                post.getImage(),
                post.getUsers().getUsername(),
                post.getCreateDateTime(),
                post.getDeletedDateTime(),
                post.getLikeCount(),
                post.getViewCount()
        ));
    }

    @Transactional
    public void likePost(Long postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Users currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        Optional<Like> existingLike = likeRepository.findByUserAndPost(currentUser, post);

        if (existingLike.isPresent()) {
            throw new RuntimeException("이미 좋아요를 눌렀습니다.");
        }

        Like like = new Like(currentUser, post);
        likeRepository.save(like);

        post.incrementLikeCount();
        log.info("현재 선택하고 있는 게시글 아이디는 " + post.getId());
        log.info("제목: " + post.getTitle());
        log.info("지금 좋아요 갯수는 " + post.getLikeCount());
    }

    @Transactional
    public void deletedPost(Long id, String username) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostExceptions.PostNotFoundException("게시글을 찾을 수 없습니다."));

        if (!post.getUsers().getUsername().equals(username)) {
            throw new PostExceptions.UnauthorizedException("게시글 삭제 권한이 없습니다.");
        }

        log.info("현재 선택하고 있는 게시글 아이디는 " + post.getId());
        post.deleteTrue();
        log.info("boolean 상태 " + post.isDeleted());
    }

    // 조회수 증가
    @Transactional
    public void incrementViewCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        post.incrementViewCount();
        postRepository.save(post);
    }

    // 최신 조회수 높은 순 조회
    public List<PostTodayPopularityDTO> getTodayTopViewedPosts() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        PageRequest pageRequest = PageRequest.of(0, 5); // 첫 페이지, 5개 항목
        return postRepository.findTodayTopViewedPosts(startOfDay, pageRequest);
    }

    // 게시글 수정
    @Transactional
    public PostWithUserDto updatePost(Long id, PostDTO postDTO, String imagePath, String username) throws PostExceptions.UnauthorizedException, PostExceptions.PostNotFoundException {
        Post post = postRepository.findById(id).orElseThrow(() -> new PostExceptions.PostNotFoundException("게시글을 찾을 수 없습니다."));

        if (!post.getUsers().getUsername().equals(username)) {
            throw new PostExceptions.UnauthorizedException("게시글 수정 권한이 없습니다.");
        }

        updatePostFields(post, postDTO, imagePath);
        Post updatedPost = postRepository.save(post);
        return new PostWithUserDto(
                updatedPost.getId(),
                updatedPost.getTitle(),
                updatedPost.getDetail(),
                updatedPost.getGame(),
                updatedPost.getImage(),
                updatedPost.getUsers().getUsername(),
                updatedPost.getCreateDateTime(),
                updatedPost.getDeletedDateTime(),
                updatedPost.getLikeCount(),
                updatedPost.getViewCount()
        );
    }

    private void updatePostFields(Post post, PostDTO postDTO, String imagePath) {
        if (postDTO.title() != null) {
            post.setTitle(postDTO.title());
        }
        if (postDTO.detail() != null) {
            post.setDetail(postDTO.detail());
        }
        if (postDTO.game() != null) {
            post.setGame(postDTO.game());
        }
        if (imagePath != null) {
            post.setImage(imagePath);
        }
    }
}
