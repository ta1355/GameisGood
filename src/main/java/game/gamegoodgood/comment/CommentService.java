package game.gamegoodgood.comment;

import game.gamegoodgood.post.Post;
import game.gamegoodgood.post.PostRepository;
import game.gamegoodgood.user.UserRepository;
import game.gamegoodgood.user.Users;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public CommentDTO createComment(Long postId, String detail, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Comment comment = new Comment(user, post, detail);
        Comment savedComment = commentRepository.save(comment);

        return new CommentDTO(
                savedComment.getIndexId(),
                savedComment.getDetail(),
                savedComment.getUsers().getUsername(),
                savedComment.getCreatedDateTime(),
                savedComment.getPost().getId()
        );
    }

    public List<CommentDTO> findAllByPostId(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        List<Comment> comments = commentRepository.findByPost(post);
        return comments.stream()
                .map(comment -> new CommentDTO(
                        comment.getIndexId(),
                        comment.getDetail(),
                        comment.getUsers().getUsername(),
                        comment.getCreatedDateTime(),
                        comment.getPost().getId()
                ))
                .collect(Collectors.toList());
    }

    public void deleteComment(Long commentId, String username){
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        if (!comment.getUsers().getUsername().equals(username)){
            throw new RuntimeException("댓글을 삭제할 권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }
}
