package game.gamegoodgood.game.steamDB.search;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import java.util.List;

public class SteamAppSearchParser {

    public List<SteamAppSearch> searchResponse(String url) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            // ParameterizedTypeReference를 사용하여 제네릭 타입을 명시적으로 지정
            List<SteamAppSearch> searchList = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<SteamAppSearch>>() {}
            ).getBody();

            return searchList;
        } catch (Exception e) {
            System.err.println("JSON 파싱 오류 또는 API 요청 오류: " + e.getMessage());
        }
        return null;
    }
}