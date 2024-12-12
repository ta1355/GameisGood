package game.gamegoodgood.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import game.gamegoodgood.comment.Comment;
import game.gamegoodgood.post.Post;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
public class Users implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long IndexId;

    @NotNull
    @Column(name = "username")
    private String username;

    @NotNull
    private String userPassword;

    @NotNull
    private String userEmail;

    private String role;

    private boolean deleted = false;

    private Timestamp loginDate;

    @CreationTimestamp
    private LocalDateTime CreateDateTime;

    private LocalDateTime DeletedDateTime;

    @OneToMany(mappedBy = "users")
    @JsonBackReference  // 'User'는 'Post'를 직렬화에서 제외하도록 설정
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "users")
    private List<Comment> comments = new ArrayList<>();

    public Long getIndexId() {
        return IndexId;
    }

    public void setIndexId(Long indexId) {
        IndexId = indexId;
    }

    public void setUsername(@NotNull String username) {
        this.username = username;
    }

    public @NotNull String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(@NotNull String userPassword) {
        this.userPassword = userPassword;
    }

    public @NotNull String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(@NotNull String userEmail) {
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

    // UserDetails 인터페이스 구현

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // ROLE_USER와 같은 역할을 부여
        return List.of(() -> this.role);  // 예시로 role을 권한으로 설정
    }

    @Override
    public String getPassword() {
        return this.userPassword;  // 암호화된 비밀번호 반환
    }

    @Override
    public String getUsername() {
        return this.username;  // 사용자 이름 반환
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;  // 계정 만료 여부
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;  // 계정 잠금 여부
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // 자격 증명 만료 여부
    }

    @Override
    public boolean isEnabled() {
        return !this.deleted;  // 삭제된 사용자일 경우 비활성화 처리
    }


}
