package game.gamegoodgood.user;

import game.gamegoodgood.config.jwt.JwtTokenProvider;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsersController {

    private final UsersService usersService;
    private final AuthenticationManager authenticationManager;
    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // 생성자를 통한 의존성 주입
    public UsersController(UsersService usersService, AuthenticationManager authenticationManager,
                           PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.usersService = usersService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 사용자 생성
    @PostMapping("/user/create")
    public ResponseEntity<UsersDTO> createUser(@RequestBody UsersDTO dto) {
        usersService.createUser(dto);
        return ResponseEntity.ok(dto);
    }

    // 로그인 처리
    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldError() != null
                    ? bindingResult.getFieldError().getDefaultMessage()
                    : "잘못된 요청";
            return ResponseEntity.status(400).body("잘못된 요청: " + errorMessage);
        }

        try {
            Authentication authentication = jwtTokenProvider.authenticateUser(
                    loginRequest.getUsername(),
                    loginRequest.getUserPassword()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.info("로그인 성공: 사용자 '{}' 인증 완료", loginRequest.getUsername());

            String token = jwtTokenProvider.generateToken(authentication);
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (BadCredentialsException e) {
            logger.error("로그인 실패: {}", e.getMessage());
            return ResponseEntity.status(401).body("로그인 실패: 아이디 또는 비밀번호가 잘못되었습니다.");
        } catch (Exception e) {
            logger.error("로그인 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("로그인 실패: " + e.getMessage());
        }
    }


    // 아이디 찾기(userEmail 사용하여 검색)
    @PostMapping("/user/find-username")
    public ResponseEntity<String> findUsername(@RequestBody UserEmailRequest userEmailRequest) {
        try {
            String username = usersService.findUsernameByEmail(userEmailRequest.userEmail());
            return ResponseEntity.ok("찾은 아이디: " + username);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // 비밀번호 변경
    @PostMapping("/user/change-password")
    public ResponseEntity<String> changePassword(@RequestBody PasswordChangeRequest request) {
        try {
            usersService.changePassword(request.username(), request.userEmail(), request.newPassword());
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

}
