package game.gamegoodgood.comment;

import com.fasterxml.jackson.annotation.JsonBackReference;
import game.gamegoodgood.post.Post;
import game.gamegoodgood.user.Users;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long indexId;

    private String detail;

    @ManyToOne
    @JoinColumn(name = "post_id")
    @JsonBackReference // 순환 참조 방지
    private Post post;

    @ManyToOne
    private Users users;

    private boolean deleted= false;

    private LocalDateTime createdDateTime= LocalDateTime.now();

    private LocalDateTime deletedDAteTime;

    public Comment() {
    }

    public Comment(Users users, Post post, String detail) {
        this.users = users;
        this.post = post;
        this.detail = detail;
    }

    public Long getIndexId() {
        return indexId;
    }

    public void setIndexId(Long indexId) {
        this.indexId = indexId;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public LocalDateTime getDeletedDAteTime() {
        return deletedDAteTime;
    }

    public void setDeletedDAteTime(LocalDateTime deletedDAteTime) {
        this.deletedDAteTime = deletedDAteTime;
    }

    public void deleteTrue() {
        this.deleted =true;
        deletedDAteTime=LocalDateTime.now();
    }

    public String getUsername() {
        return users != null ? users.getUsername() : null;
    }


}
