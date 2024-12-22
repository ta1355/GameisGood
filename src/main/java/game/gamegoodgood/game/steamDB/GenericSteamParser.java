package game.gamegoodgood.game.steamDB;

import game.gamegoodgood.game.steamDB.detail.SteamItem;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GenericSteamParser {
    public GameCategoryResponse parseCategory(String jsonResponse, String categoryKey) {
        List<SteamItem> items = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            if (jsonObject.has(categoryKey)) {
                JSONObject categoryJson = jsonObject.getJSONObject(categoryKey);
                items = parseCategoryItems(categoryJson.getJSONArray("items"));
                return new GameCategoryResponse(items);
            } else {
                System.err.println(categoryKey + " 데이터 없음 확인 바람");
            }
        } catch (Exception e) {
            System.err.println("JSON 파싱 오류 빈 부분을 확인바람: " + e.getMessage());
        }
        return null;
    }

    private List<SteamItem> parseCategoryItems(JSONArray itemsJson) {
        List<SteamItem> items = new ArrayList<>();
        for (int i = 0; i < itemsJson.length(); i++) {
            JSONObject itemJson = itemsJson.getJSONObject(i);
            SteamItem steamItem = new SteamItem(
                    itemJson.getInt("id"),
                    itemJson.getString("name"),
                    itemJson.getBoolean("discounted"),
                    itemJson.optInt("discount_percent", 0),
                    itemJson.optInt("original_price", 0),
                    itemJson.getInt("final_price"),
                    itemJson.getString("currency"),
                    itemJson.optString("large_capsule_image", null),
                    itemJson.optString("small_capsule_image", null),
                    itemJson.getBoolean("windows_available"),
                    itemJson.getBoolean("mac_available"),
                    itemJson.getBoolean("linux_available"),
                    itemJson.getBoolean("streamingvideo_available"),
                    itemJson.optLong("discount_expiration", 0L),
                    itemJson.optString("header_image", null),
                    itemJson.optString("headline", null)
            );
            items.add(steamItem);
        }
        return items;
    }
}