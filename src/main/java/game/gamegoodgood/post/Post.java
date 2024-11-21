package game.gamegoodgood.post;

import game.gamegoodgood.comment.Comment;
import game.gamegoodgood.user.Users;
import jakarta.persistence.*;
import org.springframework.lang.Nullable;

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

    @Nullable
    private String image;

    @ManyToOne
    private Users users;

    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();

    public Post() {
    }

    public Post(String title, String detail, String game) {
        this.title = title;
        this.detail = detail;
        this.Game = game;
    }

    public Post(String title, String detail, String game, String image) {
        this.title = title;
        this.detail = detail;
        this.Game = game;
        this.image = image;
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void setCreateDateTime(LocalDateTime createDateTime) {
        this.createDateTime = createDateTime;
    }

    public void setDeletedDateTime(LocalDateTime deletedDateTime) {
        this.deletedDateTime = deletedDateTime;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public String getGame() {
        return Game;
    }

    public void setGame(String game) {
        Game = game;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
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
