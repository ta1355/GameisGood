package game.gamegoodgood.config.auth;

import game.gamegoodgood.user.UserRepository;
import game.gamegoodgood.user.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service
public class OAuth2LoginService {

    private final UserRepository usersRepository;

    @Autowired
    public OAuth2LoginService(UserRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    // 구글 로그인 후 사용자 정보를 DB에 저장하거나 업데이트
    public Users saveOrUpdateUser(String userEmail, String username, String role) {
        // 먼저 해당 이메일로 사용자가 존재하는지 확인
        Users user = usersRepository.findByUserEmail(userEmail)
                .orElseGet(() -> new Users()); // 존재하지 않으면 새 객체 생성

        // 사용자 정보 업데이트
        user.setUserEmail(userEmail);
        user.setUsername(username);
        user.setRole(role);
        user.setCreateDateTime(java.time.LocalDateTime.now());
        user.setDeleted(false); // 새로 추가된 사용자는 deleted가 false로 설정

        return usersRepository.save(user); // 저장
    }
}