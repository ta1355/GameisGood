package game.gamegoodgood.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long> {

    public Users findByUsername(String username);

    public Users findByUserEmail(String userEmail);
}
