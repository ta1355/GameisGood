package game.gamegoodgood.post;

import game.gamegoodgood.post.like.Like;
import game.gamegoodgood.post.like.LikeRepository;
import game.gamegoodgood.user.UserRepository;
import game.gamegoodgood.user.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class postService {

    private static final Logger log = LoggerFactory.getLogger(postService.class);
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;

    public postService(PostRepository postRepository, UserRepository userRepository, LikeRepository likeRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
    }

    public PostWithUserDto savePost(PostDto dto) {
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
        Page<Post> postPage = postRepository.findAll(pageable);
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
    public void deletedPost(Long id) {
        Post post = postRepository.findById(id).orElse(null);
        if (post != null) {
            log.info("현재 선택하고 있는 게시글 아이디는 " + post.getId());
            post.deleteTrue();
            log.info("boolean 상태 " + post.isDeleted());
        }
    }

    // 조회수 증가
    @Transactional
    public void incrementViewCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        post.incrementViewCount();
        postRepository.save(post);
    }


}
