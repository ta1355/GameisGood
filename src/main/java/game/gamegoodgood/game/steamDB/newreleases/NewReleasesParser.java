package game.gamegoodgood.game.steamDB.newreleases;

import game.gamegoodgood.game.steamDB.special.GameCategoryResponse;
import game.gamegoodgood.game.steamDB.special.SteamItem;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewReleasesParser {

    public GameCategoryResponse parse(String jsonResponse) {
        List<SteamItem> specials = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);

            if (jsonObject.has("new_releases")) {
                JSONObject specialsJson = jsonObject.getJSONObject("new_releases");
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


    private List<SteamItem> parseItems(JSONArray itemsJson) {
        List<SteamItem> specials = new ArrayList<>();
        for (int i = 0; i < itemsJson.length(); i++) {
            JSONObject itemJson = itemsJson.getJSONObject(i);
            SteamItem steamItem = parseTopSellersItem(itemJson);
            specials.add(steamItem);
        }
        return specials;
    }


    private SteamItem parseTopSellersItem(JSONObject itemJson) {
        return new SteamItem(
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
                itemJson.optLong("discount_expiration" , 0L),
                itemJson.optString("header_image", null),
                itemJson.optString("headline", null)
        );
    }
}
