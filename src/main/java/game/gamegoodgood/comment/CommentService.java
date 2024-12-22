package game.gamegoodgood.comment;

import game.gamegoodgood.post.Post;
import game.gamegoodgood.post.PostRepository;
import game.gamegoodgood.user.UserRepository;
import game.gamegoodgood.user.Users;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;  // PostRepository 추가
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    // 댓글 생성
    public Comment createComment(Post post, String detail, Users user) {
        Comment comment = new Comment(user, post, detail);
        return commentRepository.save(comment); // 댓글을 저장 후 반환
    }

    // 댓글 조회
    public List<Comment> findAllByPost(Post post) {
        return commentRepository.findByPost(post); // 게시글에 달린 모든 댓글을 조회
    }
}
