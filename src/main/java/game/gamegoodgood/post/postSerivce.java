package game.gamegoodgood.post;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class postSerivce {

    private static final Logger log = LoggerFactory.getLogger(postSerivce.class);
    private final PostRepository postRepository;

    public postSerivce(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Post savePost(PostDto dto) {
        String image = dto.image();

        if (image == null || image.isEmpty()) {
            image = null;
        }
        Post post = new Post(dto.title(), dto.detail(), dto.game(), image);
        return postRepository.save(post);
    }

    @Transactional
    public void likePost(Long id) {
        Post post = postRepository.findById(id).orElse(null);
        log.info(String.valueOf("현재 선택하고 있는 게시글 아이디는 " + post.getId()));
        log.info("제목 " + post.getTitle());
        post.like();
        log.info(String.valueOf("지금 좋아요 갯수는 " + post.getLikeCount()));
    }

    @Transactional
    public void deletedPost(Long id) {
        Post post = postRepository.findById(id).orElse(null);
        log.info(String.valueOf("현재 선택하고 있는 게시글 아이디는 " + post.getId()));
        post.deleteTrue();
        log.info("boolean 상태 " + post.isDeleted());

    }

    public Post findById(Long id) {
        Post post = postRepository.findByIdAndDeletedFalse(id).orElse(null);
        return post;
    }

    public List<Post> findAll() {
        List<Post> all = postRepository.findAll();
        return all;
    }
}
