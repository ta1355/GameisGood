package game.gamegoodgood.user;

import game.gamegoodgood.comment.Comment;
import game.gamegoodgood.post.Post;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.security.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long IndexId;

    private String username;

    private String userPassword;

    private String userEmail;

    private String role;

    private boolean deleted =false;

    private Timestamp loginDate;

    @CreationTimestamp
    private LocalDateTime CreateDateTime;

    private LocalDateTime DeletedDateTime;

    @OneToMany(mappedBy = "users")
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "users")
    private List<Comment> comments = new ArrayList<>();

    public void deleteTrue() {
        this.deleted = true;
    }

    public Users() {
    }

    public Users(String username, String userPassword, String userEmail) {
        this.username = username;
        this.userPassword = userPassword;
        this.userEmail = userEmail;
    }

    public Long getIndexId() {
        return IndexId;
    }

    public void setIndexId(Long indexId) {
        IndexId = indexId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Timestamp getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Timestamp loginDate) {
        this.loginDate = loginDate;
    }

    public LocalDateTime getCreateDateTime() {
        return CreateDateTime;
    }

    public void setCreateDateTime(LocalDateTime createDateTime) {
        CreateDateTime = createDateTime;
    }

    public LocalDateTime getDeletedDateTime() {
        return DeletedDateTime;
    }

    public void setDeletedDateTime(LocalDateTime deletedDateTime) {
        DeletedDateTime = deletedDateTime;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
