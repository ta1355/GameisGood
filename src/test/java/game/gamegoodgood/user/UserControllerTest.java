package game.gamegoodgood.user;

import game.gamegoodgood.config.auth.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

        ResponseEntity<?> response = usersController.createUser(dto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testLoginSuccess() {
        // Given
        LoginRequest loginRequest = new LoginRequest("testUser", "password");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        Authentication authentication = mock(Authentication.class);
        // authenticateUser 메서드에 대한 stub 추가
        when(jwtTokenProvider.authenticateUser(loginRequest.getUsername(), loginRequest.getUserPassword()))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("testToken");

        // When
        ResponseEntity<?> response = usersController.login(loginRequest, bindingResult);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof JwtResponse);
        assertEquals("testToken", ((JwtResponse) response.getBody()).getToken());

        // 검증
        verify(jwtTokenProvider).authenticateUser(loginRequest.getUsername(), loginRequest.getUserPassword());
        verify(jwtTokenProvider).generateToken(authentication);
    }

    @Test
    void testLoginFailure() {
        // Given
        LoginRequest loginRequest = new LoginRequest("testUser", "wrongPassword");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        // authenticateUser가 예외를 던지도록 설정
        when(jwtTokenProvider.authenticateUser(loginRequest.getUsername(), loginRequest.getUserPassword()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // When
        ResponseEntity<?> response = usersController.login(loginRequest, bindingResult);

        // Then
        assertEquals(401, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof String);
        assertTrue(((String) response.getBody()).contains("로그인 실패"));
    }

    @Test
    void testLoginWithInvalidRequest() {
        LoginRequest loginRequest = new LoginRequest("", "");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldError()).thenReturn(null);

        ResponseEntity<?> response = usersController.login(loginRequest, bindingResult);

        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof String);
        assertEquals("잘못된 요청: 잘못된 요청", response.getBody());
    }
}
