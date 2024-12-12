package game.gamegoodgood.comment;

import org.springframework.stereotype.Service;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Comment createComment(CommentDto dto){
         Comment comment = new Comment(dto.detail());
       return commentRepository.save(comment);
    }
}
