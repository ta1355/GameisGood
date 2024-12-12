package game.gamegoodgood.post;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SomeService {

    public void someMethod() {
        // SecurityContextHolder에서 현재 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            // 현재 사용자의 이름(사용자 ID 또는 이메일)을 가져옴
            String username = authentication.getName();
            System.out.println("현재 로그인한 사용자: " + username);
        } else {
            System.out.println("현재 인증된 사용자가 없습니다.");
        }
    }
}
