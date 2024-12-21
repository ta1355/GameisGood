package game.gamegoodgood.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UsersServiceTest {

    @InjectMocks
    private UsersService usersService;

    @Mock
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        passwordEncoder = new BCryptPasswordEncoder();
        usersService = new UsersService(userRepository);
    }

    @Test
    void testLoadUserByUsername_Success() {
        String username = "testUser";
        Users user = new Users(username, "password", "test@example.com");
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        UserDetails userDetails = usersService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        String username = "nonExistentUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            usersService.loadUserByUsername(username);
        });
    }

    @Test
    void testCreateUser_Success() {
        UsersDTO dto = new UsersDTO("newUser", "password", "new@example.com");
        when(userRepository.findByUsername(dto.username())).thenReturn(Optional.empty());

        when(userRepository.save(any(Users.class))).thenAnswer(invocation -> {
            Users savedUser = invocation.getArgument(0);
            return savedUser;
        });

        Users createdUser = usersService.createUser(dto);

        assertNotNull(createdUser);
        assertEquals(dto.username(), createdUser.getUsername());
        assertTrue(passwordEncoder.matches(dto.userPassword(), createdUser.getUserPassword()));
        assertEquals(dto.userEmail(), createdUser.getUserEmail());
        assertEquals("USER", createdUser.getRole());
    }

    @Test
    void testCreateUser_UserAlreadyExists() {
        UsersDTO dto = new UsersDTO("existingUser", "password", "existing@example.com");
        when(userRepository.findByUsername(dto.username())).thenReturn(Optional.of(new Users()));

        assertThrows(RuntimeException.class, () -> {
            usersService.createUser(dto);
        });
    }
}
