package game.gamegoodgood.post;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PostController {

    private final game.gamegoodgood.post.postSerivce postSerivce;

    public PostController(game.gamegoodgood.post.postSerivce postSerivce) {
        this.postSerivce = postSerivce;
    }

    //아이디로 찾기
    @GetMapping("/post/{id}")
    public Post findPostById(@PathVariable Long id){
       return postSerivce.findById(id);
    }

    //전체 찾기
    @GetMapping("/post")
    public List<Post> findPostAll() {
        return  postSerivce.findAll();
    }

    //게시글 만들기
    @PostMapping("/createpost")
    public void create(@RequestBody PostDto dto) {
        postSerivce.savePost(dto);
    }

    //게시글 좋아요
    @GetMapping("postlike/{id}")
    public void likePost(@PathVariable Long id) {
        postSerivce.likePost(id);
    }

    //게시글 삭제
    @DeleteMapping("postdelete/{id}")
    public void deletePost(@PathVariable Long id){
        postSerivce.deletedPost(id);
    }
}
