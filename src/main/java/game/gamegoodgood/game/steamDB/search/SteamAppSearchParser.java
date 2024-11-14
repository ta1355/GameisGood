package game.gamegoodgood.game.steamDB.search;

import org.springframework.web.client.RestTemplate;
import java.util.List;

public class SteamAppSearchParser {

    public List<SteamAppSearch> searchResponse(String url) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            List<SteamAppSearch> searchList = restTemplate.getForObject(url, List.class);
            return searchList;
        } catch (Exception e) {
            System.err.println("JSON 파싱 오류 또는 API 요청 오류: " + e.getMessage());
        }
        return null;
    }
}