package game.gamegoodgood.post;

import game.gamegoodgood.post.like.Like;
import game.gamegoodgood.post.like.LikeRepository;
import game.gamegoodgood.user.UserRepository;
import game.gamegoodgood.user.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    // 게시글 저장 메서드
    public PostWithUserDto savePost(PostDto dto) {
        String image = dto.image();
        if (image == null || image.isEmpty()) {
            image = null;
        }

        // SecurityContextHolder를 통해 현재 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("로그인 정보가 없습니다.");
        }
        String username = authentication.getName();  // 로그인한 사용자 이름

        // 사용자 정보 조회
        Users currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 새로운 게시글 생성
        Post post = new Post(dto.title(), dto.detail(), dto.game(), image);
        post.setUsers(currentUser);  // 현재 사용자 설정

        // 게시글 저장
        Post savedPost = postRepository.save(post);

        // 저장된 게시글과 사용자 이름을 포함한 DTO 반환
        return new PostWithUserDto(
                savedPost.getId(),
                savedPost.getTitle(),
                savedPost.getDetail(),
                savedPost.getGame(),
                savedPost.getImage(),
                currentUser.getUsername(),
                savedPost.getCreateDateTime(),
                savedPost.getDeletedDateTime(),
                savedPost.getLikeCount()
        );
    }

    // 게시글 조회 메서드 (username 포함)
    public PostWithUserDto findById(Long id) {
        Post post = postRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        Users user = post.getUsers();  // 게시글의 사용자 정보

        // 사용자 이름을 포함한 DTO 반환
        return new PostWithUserDto(
                post.getId(),
                post.getTitle(),
                post.getDetail(),
                post.getGame(),
                post.getImage(),
                user.getUsername(),
                post.getCreateDateTime(),
                post.getDeletedDateTime(),
                post.getLikeCount()
        );
    }

    // 모든 게시글 조회 메서드 (PostWithUserDto 리스트 반환)
    public List<PostWithUserDto> findAll() {
        List<Post> posts = postRepository.findAll();

        // Post 객체들을 PostWithUserDto로 변환하여 반환
        return posts.stream().map(post -> {
            Users user = post.getUsers();  // 게시글의 사용자 정보
            return new PostWithUserDto(
                    post.getId(),
                    post.getTitle(),
                    post.getDetail(),
                    post.getGame(),
                    post.getImage(),
                    user.getUsername(),
                    post.getCreateDateTime(),
                    post.getDeletedDateTime(),
                    post.getLikeCount()
            );
        }).collect(Collectors.toList());
    }

    @Transactional
    public void likePost(Long postId) {
        // 로그인한 사용자 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Users currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 게시글 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        // 이미 해당 게시글에 대해 좋아요를 눌렀는지 확인
        Optional<Like> existingLike = likeRepository.findByUserAndPost(currentUser, post);

        if (existingLike.isPresent()) {
            throw new RuntimeException("이미 좋아요를 눌렀습니다.");
        }

        // 좋아요 추가
        Like like = new Like(currentUser, post);
        likeRepository.save(like);

        // 게시글 좋아요 수 증가
        post.like();
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
}

