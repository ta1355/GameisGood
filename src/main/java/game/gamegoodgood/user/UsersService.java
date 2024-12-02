package game.gamegoodgood.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsersService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UsersService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Users createUser(UsersDTO dto) {
        Users users = new Users(dto.username(), dto.userPassword(), dto.userEmail());
        users.setRole("ADMIN");

        String rawPassword = users.getUserPassword();
        String encPassword = passwordEncoder.encode(rawPassword);
        users.setUserPassword(encPassword);

        return userRepository.save(users);
    }
}
