package game.gamegoodgood.user;

import game.gamegoodgood.config.auth.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class UsersController {

    private final UsersService usersService;
    private final AuthenticationManager authenticationManager;
    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public UsersController(UsersService usersService, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
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
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult) {
        // 유효성 검사 실패 시
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(400).body("잘못된 요청: " + bindingResult.getFieldError().getDefaultMessage());
        }

        try {
            // UserDetails를 통해 사용자 정보 조회
            UserDetails userDetails = usersService.loadUserByUsername(loginRequest.getUsername());

            // 로그인 요청 로그 추가
            logger.info("로그인 요청: username = {}, password = {}", loginRequest.getUsername(), loginRequest.getUserPassword());

            // 비밀번호 확인
            if (!passwordEncoder.matches(loginRequest.getUserPassword(), userDetails.getPassword())) {
                logger.warn("로그인 실패: 사용자 '{}'의 비밀번호가 일치하지 않습니다.", loginRequest.getUsername());
                return ResponseEntity.status(401).body("로그인 실패: 아이디 또는 비밀번호가 잘못되었습니다.");
            }

            // 인증 객체 생성 및 인증 처리
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getUserPassword()
                    )
            );

            // 인증 정보 보관
            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.info("로그인 성공: 사용자 '{}' 인증 완료", loginRequest.getUsername());

            // JWT 토큰 생성
            String token = jwtTokenProvider.generateToken(authentication);

            // JWT 토큰을 포함한 응답
            return ResponseEntity.ok(new JwtResponse(token));  // JwtResponse 객체 반환

        } catch (UsernameNotFoundException ex) {
            // 사용자 없을 때 예외 처리
            logger.warn("로그인 실패: 사용자 '{}'가 존재하지 않습니다.", loginRequest.getUsername());
            return ResponseEntity.status(401).body("로그인 실패: 아이디 또는 비밀번호가 잘못되었습니다.");
        } catch (Exception e) {
            // 예기치 않은 오류 처리
            logger.error("로그인 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(401).body("로그인 실패: " + e.getMessage());
        }
    }
}
