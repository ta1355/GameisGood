package game.gamegoodgood.user;

import game.gamegoodgood.config.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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

        ResponseEntity<UsersDTO> response = usersController.createUser(dto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testLoginSuccess() {
        LoginRequest loginRequest = new LoginRequest("testUser", "password");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(usersService.isAccountLocked("testUser")).thenReturn(false);

        Authentication authentication = mock(Authentication.class);
        when(jwtTokenProvider.authenticateUser(loginRequest.getUsername(), loginRequest.getUserPassword()))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("testToken");

        ResponseEntity<Object> response = usersController.login(loginRequest, bindingResult);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof JwtResponse);
        assertEquals("testToken", ((JwtResponse) response.getBody()).getToken());

        verify(usersService).updateLastLoginDate("testUser");
    }

    @Test
    void testLoginFailure() {
        LoginRequest loginRequest = new LoginRequest("testUser", "wrongPassword");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(usersService.isAccountLocked("testUser")).thenReturn(false);

        when(jwtTokenProvider.authenticateUser(loginRequest.getUsername(), loginRequest.getUserPassword()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        ResponseEntity<Object> response = usersController.login(loginRequest, bindingResult);

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

        ResponseEntity<Object> response = usersController.login(loginRequest, bindingResult);

        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof String);
        assertEquals("잘못된 요청: 잘못된 요청", response.getBody());
    }

    @Test
    void testLoginWithLockedAccount() {
        LoginRequest loginRequest = new LoginRequest("testUser", "password");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(usersService.isAccountLocked("testUser")).thenReturn(true);

        ResponseEntity<Object> response = usersController.login(loginRequest, bindingResult);

        assertEquals(401, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof String);
        assertEquals("계정이 잠겼습니다. 관리자에게 문의하세요.", response.getBody());
    }

    @Test
    void testFindUsername() {
        UserEmailRequest request = new UserEmailRequest("test@example.com");
        when(usersService.findUsernameByEmail("test@example.com")).thenReturn("testUser");

        ResponseEntity<String> response = usersController.findUsername(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("찾은 아이디: testUser", response.getBody());
    }

    @Test
    void testFindUsernameNotFound() {
        UserEmailRequest request = new UserEmailRequest("nonexistent@example.com");
        when(usersService.findUsernameByEmail("nonexistent@example.com"))
                .thenThrow(new RuntimeException("해당 이메일로 등록된 사용자를 찾을 수 없습니다."));

        ResponseEntity<String> response = usersController.findUsername(request);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("해당 이메일로 등록된 사용자를 찾을 수 없습니다.", response.getBody());
    }

    @Test
    void testChangePasswordSuccess() {
        PasswordChangeRequest request = new PasswordChangeRequest("testUser", "test@example.com", "newPassword");
        doNothing().when(usersService).changePassword("testUser", "test@example.com", "newPassword");

        ResponseEntity<String> response = usersController.changePassword(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("비밀번호가 성공적으로 변경되었습니다.", response.getBody());
    }

    @Test
    void testChangePasswordFailure() {
        PasswordChangeRequest request = new PasswordChangeRequest("testUser", "wrong@example.com", "newPassword");
        doThrow(new RuntimeException("이메일 주소가 일치하지 않습니다."))
                .when(usersService).changePassword("testUser", "wrong@example.com", "newPassword");

        ResponseEntity<String> response = usersController.changePassword(request);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("이메일 주소가 일치하지 않습니다.", response.getBody());
    }
}
