package game.gamegoodgood.user;

import game.gamegoodgood.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public Users createUser(UsersDTO dto) {
        // 사용자 이름이 이미 존재하는지 확인
        if (userRepository.findByUsername(dto.username()).isPresent()) {
            throw new RuntimeException("이미 존재하는 사용자입니다.");
        }

        // 비밀번호 암호화
        String rawPassword = dto.password();
        String encPassword = passwordEncoder.encode(rawPassword);

        // 새로운 사용자 저장
        Users users = new Users(dto.username(), encPassword, dto.email());
        users.setRole("USER"); // 기본 역할 설정
        return userRepository.save(users);
    }

    public String findUsernameByEmail(String email) {
        return userRepository.findByUserEmail(email)
                .map(Users::getUsername)
                .orElseThrow(() -> new RuntimeException("해당 이메일로 등록된 사용자를 찾을 수 없습니다."));
    }

    public void changePassword(String username, String userEmail, String newPassword) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (!user.getUserEmail().equals(userEmail)) {
            throw new RuntimeException("이메일 주소가 일치하지 않습니다.");
        }

        String encodedNewPassword = passwordEncoder.encode(newPassword);
        user.setUserPassword(encodedNewPassword);
        userRepository.save(user);
    }

    @Transactional
    public void updateLastLoginDate(String username) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        user.setLastLoginDate(LocalDateTime.now());
        userRepository.save(user);
    }

    public boolean isAccountLocked(String username) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        LocalDateTime lastLoginDate = user.getLastLoginDate();
        if (lastLoginDate == null) {
            return false; // 새 계정의 경우
        }
        return ChronoUnit.DAYS.between(lastLoginDate, LocalDateTime.now()) >= 365;
    }

}
