package game.gamegoodgood.post.like;


import game.gamegoodgood.post.Post;
import game.gamegoodgood.user.Users;
import jakarta.persistence.*;


@Entity
@Table(name = "post_like")
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    private boolean isLiked;

    public Like(Users user, Post post) {
        this.user = user;
        this.post = post;
        this.isLiked = true;  // 기본적으로 좋아요 눌렀다고 설정
    }

    // Getter, Setter
    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }
}