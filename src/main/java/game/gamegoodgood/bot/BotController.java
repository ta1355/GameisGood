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
    public String sendMessageToDiscord(@RequestBody MessageRequest messageRequest) {
        // RestTemplate을 사용하여 Flask 서버에 POST 요청을 보냄
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MessageRequest> request = new HttpEntity<>(messageRequest, headers);

        // Flask 서버의 /send-message 엔드포인트로 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(DISCORD_BOT_URL + "/send-message", HttpMethod.POST, request, String.class);
        return response.getBody();  // 디스코드 봇의 응답 반환
    }

}
