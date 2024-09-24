package game.gamegoodgood.post;

import game.gamegoodgood.comment.Comment;
import game.gamegoodgood.user.Users;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String detail;

    private boolean deleted =false;

    private LocalDateTime createDateTime=LocalDateTime.now();

    private LocalDateTime deletedDateTime;

    private int likeCount;

    private String Game;

    @ManyToOne
    private Users users;


    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();

    public Post() {
    }

    public Post(String title, String detail, String game) {
        this.title = title;
        this.detail = detail;
        Game = game;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detail;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public LocalDateTime getCreateDateTime() {
        return createDateTime;
    }


    public LocalDateTime getDeletedDateTime() {
        return deletedDateTime;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void deleteTrue() {
        this.deleted = true;
        this.deletedDateTime= LocalDateTime.now();
    }

    public void like(){
        likeCount+=1;
    }


}
