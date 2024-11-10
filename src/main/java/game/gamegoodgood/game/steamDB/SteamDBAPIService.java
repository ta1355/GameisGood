package game.gamegoodgood.game.steamDB;


import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class SteamDBAPIService {
    private final String apiUrl = "https://store.steampowered.com/api/featuredcategories?cc=KR&l=korean";
    private final SpecialsParser specialsParser = new SpecialsParser();
    private final DetailParser detailParser = new DetailParser();

    public GameCategoryResponse findSteamAPI() {
        String jsonResponse = fetchDataFromApi();
        return specialsParser.parse(jsonResponse);
    }

    private String fetchDataFromApi() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(apiUrl, String.class);
    }

    public DetailItem findDetail(Long id) {
        String url = "https://store.steampowered.com/api/appdetails?appids="+id+"&cc=KR&l=korean";
        System.out.println("url 확인용: " +url);
        return detailParser.detailResponse(url);
    }
}