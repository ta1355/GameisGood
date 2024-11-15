package game.gamegoodgood.game.steamDB.special;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class SpecialsParser {


    public GameCategoryResponse parse(String url) {
        List<SpecialItem> specials = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();
        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            JSONObject jsonObject = new JSONObject(jsonResponse);

            if (jsonObject.has("specials")) {
                JSONObject specialsJson = jsonObject.getJSONObject("specials");
                specials = parseItems(specialsJson.getJSONArray("items"));
                return new GameCategoryResponse(specials);
            }else {
                System.err.println("데이터 없음 확인 바람");
            }
        } catch (Exception e) {
            System.err.println("JSON 파싱 오류 빈 부분을 확인바람: " + e.getMessage());
        }
        return null;
    }


    private List<SpecialItem> parseItems(JSONArray itemsJson) {
        List<SpecialItem> specials = new ArrayList<>();
        for (int i = 0; i < itemsJson.length(); i++) {
            JSONObject itemJson = itemsJson.getJSONObject(i);
            SpecialItem specialItem = parseSpecialItem(itemJson);
            specials.add(specialItem);
        }
        return specials;
    }


    private SpecialItem parseSpecialItem(JSONObject itemJson) {
        return new SpecialItem(
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
    }
}
