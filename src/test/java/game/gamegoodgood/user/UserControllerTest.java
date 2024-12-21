package game.gamegoodgood.user;

import game.gamegoodgood.config.auth.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UsersControllerTest {

    @InjectMocks
    private UsersController usersController;

    @Mock
    private UsersService usersService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser() {
        UsersDTO dto = new UsersDTO("testUser", "password", "test@example.com");
        when(usersService.createUser(dto)).thenReturn(new Users("testUser", "encodedPassword", "test@example.com"));

        ResponseEntity<UsersDTO> response = usersController.createUser(dto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testLoginSuccess() {
        LoginRequest loginRequest = new LoginRequest("testUser", "password");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        UserDetails userDetails = mock(UserDetails.class);
        when(usersService.loadUserByUsername("testUser")).thenReturn(userDetails);
        when(passwordEncoder.matches("password", userDetails.getPassword())).thenReturn(true);

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        when(jwtTokenProvider.generateToken(authentication)).thenReturn("testToken");

        ResponseEntity<?> response = usersController.login(loginRequest, bindingResult);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof JwtResponse);
        assertEquals("testToken", ((JwtResponse) response.getBody()).getToken());
    }

    @Test
    void testLoginFailureWrongPassword() {
        LoginRequest loginRequest = new LoginRequest("testUser", "wrongPassword");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        UserDetails userDetails = mock(UserDetails.class);
        when(usersService.loadUserByUsername("testUser")).thenReturn(userDetails);
        when(passwordEncoder.matches("wrongPassword", userDetails.getPassword())).thenReturn(false);

        ResponseEntity<?> response = usersController.login(loginRequest, bindingResult);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("로그인 실패: 아이디 또는 비밀번호가 잘못되었습니다.", response.getBody());
    }

    @Test
    void testLoginFailureUserNotFound() {
        LoginRequest loginRequest = new LoginRequest("nonExistentUser", "password");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        when(usersService.loadUserByUsername("nonExistentUser")).thenThrow(new UsernameNotFoundException("User not found"));

        ResponseEntity<?> response = usersController.login(loginRequest, bindingResult);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("로그인 실패: 아이디 또는 비밀번호가 잘못되었습니다.", response.getBody());
    }
}
