package game.gamegoodgood.user;

import game.gamegoodgood.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsersService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UsersService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 'Users' 객체는 이미 UserDetails를 구현하므로, User 클래스로 변환할 필요 없음
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public Users createUser(UsersDTO dto) {
        // 사용자 이름이 이미 존재하는지 확인
        if (userRepository.findByUsername(dto.username()).isPresent()) {
            throw new RuntimeException("이미 존재하는 사용자입니다.");
        }

        // 비밀번호 암호화
        String rawPassword = dto.userPassword();
        String encPassword = passwordEncoder.encode(rawPassword);

        // 새로운 사용자 저장
        Users users = new Users(dto.username(), encPassword, dto.userEmail());
        users.setRole("USER"); // 기본 역할 설정
        return userRepository.save(users);
    }
}
