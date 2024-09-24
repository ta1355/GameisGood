package game.gamegoodgood.comment;

import game.gamegoodgood.post.Post;
import game.gamegoodgood.user.Users;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String detail;

    @ManyToOne
    private Post post;

    @ManyToOne
    private Users users;

    private boolean deleted= false;

    private LocalDateTime createdDateTime= LocalDateTime.now();

    private LocalDateTime deletedDAteTime;


    public Long getId() {
        return id;
    }

    public String getDetail() {
        return detail;
    }

    public Post getPost() {
        return post;
    }

    public Users getUser() {
        return users;
    }

    public void deleteTrue() {
        this.deleted =true;
        deletedDAteTime=LocalDateTime.now();
    }
}
