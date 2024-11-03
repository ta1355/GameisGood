package game.gamegoodgood.game.steamDB;


import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class SteamDBAPIService {
    private final String apiUrl = "https://store.steampowered.com/api/featuredcategories";

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
        JSONObject jsonObject = new JSONObject(jsonResponse);


        if (jsonObject.has("specials")) {
            JSONObject specialsJson = jsonObject.getJSONObject("specials");
            JSONArray itemsJson = specialsJson.getJSONArray("items");


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
                        itemJson.optString("large_capsule_image", null), // 새로운 필드
                        itemJson.optString("small_capsule_image", null), // 새로운 필드
                        itemJson.getBoolean("windows_available"), // 새로운 필드
                        itemJson.getBoolean("mac_available"), // 새로운 필드
                        itemJson.getBoolean("linux_available"), // 새로운 필드
                        itemJson.getBoolean("streamingvideo_available"), // 새로운 필드
                        itemJson.getLong("discount_expiration"), // 새로운 필드
                        itemJson.optString("header_image", null), // 새로운 필드
                        itemJson.optString("headline", null) // 새로운 필드
                );
                specials.add(specialItem);
            }
        }

        return new GameCategoryResponse(specials);
    }
}