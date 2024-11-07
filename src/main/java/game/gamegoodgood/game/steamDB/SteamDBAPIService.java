package game.gamegoodgood.game.steamDB;


import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class SteamDBAPIService {
    private final String apiUrl = "https://store.steampowered.com/api/featuredcategories?cc=KR";

    public GameCategoryResponse findSteamAPI() {
        String jsonResponse = fetchDataFromApi();
        return parseSpecials(jsonResponse);
    }

    private String fetchDataFromApi() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(apiUrl, String.class);
    }

    private GameCategoryResponse parseSpecials(String jsonResponse) {
        List<SpecialItem> specials = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);

            if (jsonObject.has("specials")) {
                JSONObject specialsJson = jsonObject.getJSONObject("specials");
                JSONArray itemsJson = specialsJson.getJSONArray("items");

                // 아이템을 순차적으로 처리
                for (int i = 0; i < itemsJson.length(); i++) {
                    JSONObject itemJson = itemsJson.getJSONObject(i);
                    SpecialItem specialItem = new SpecialItem(
                            itemJson.getInt("id"),
                            itemJson.getString("name"),
                            itemJson.getBoolean("discounted"),
                            itemJson.getInt("discount_percent"),
                            itemJson.getInt("original_price"),
                            itemJson.getInt("final_price"),
                            itemJson.getString("currency"),
                            itemJson.optString("large_capsule_image", null),
                            itemJson.optString("small_capsule_image", null),
                            itemJson.getBoolean("windows_available"),
                            itemJson.getBoolean("mac_available"),
                            itemJson.getBoolean("linux_available"),
                            itemJson.getBoolean("streamingvideo_available"),
                            itemJson.getLong("discount_expiration"),
                            itemJson.optString("header_image", null),
                            itemJson.optString("headline", null)
                    );
                    specials.add(specialItem);
                }
            }
        } catch (Exception e) {
            System.err.println("JSON 파싱 오류 빈 부분을 확인바람: " + e.getMessage());
            return new GameCategoryResponse(new ArrayList<>());
        }

        return new GameCategoryResponse(specials);
    }
}