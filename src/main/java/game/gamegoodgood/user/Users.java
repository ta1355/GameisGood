package game.gamegoodgood.user;

import game.gamegoodgood.comment.Comment;
import game.gamegoodgood.post.Post;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private String userPassword;

    private boolean deleted =false;

    private LocalDateTime CreateDateTime;

    private LocalDateTime DeletedDateTime;

    @OneToMany(mappedBy = "users")
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "users")
    private List<Comment> comments = new ArrayList<>();

    public void deleteTrue() {
        this.deleted = true;
    }



}
