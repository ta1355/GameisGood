package game.gamegoodgood.bot;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class BotController {

    private final String DISCORD_BOT_URL = "http://localhost:5000";

    @PostMapping("/sendMessage")
    public ResponseEntity<MessageResponse> sendMessageToDiscord(@RequestBody MessageRequest messageRequest) {

        RestTemplate restTemplate = new RestTemplate();

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 본문 설정 (MessageRequest 객체를 JSON으로 변환하여 전달)
        HttpEntity<MessageRequest> request = new HttpEntity<>(messageRequest, headers);

        try {
            // Flask 서버의 /send-message 엔드포인트로 POST 요청 보내기
            ResponseEntity<MessageResponse> response = restTemplate.exchange(
                    DISCORD_BOT_URL + "/send-message",  // Flask 서버의 엔드포인트
                    HttpMethod.POST,  // POST 요청
                    request,  // 요청 본문
                    MessageResponse.class  // 응답을 MessageResponse 클래스로 처리
            );

            // Flask 서버로부터 받은 응답을 클라이언트에 반환
            return new ResponseEntity<>(response.getBody(), HttpStatus.OK);

        } catch (Exception e) {
            // 예외 처리: 오류 메시지 반환
            MessageResponse errorResponse = new MessageResponse();
            errorResponse.setStatus("error");
            errorResponse.setMessage("Error communicating with Flask server: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
