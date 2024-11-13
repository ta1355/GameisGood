package game.gamegoodgood.game.steamDB;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailParser {

    private Long extractIdFromUrl(String url) {
        int startIndex = url.indexOf("appids=");
        if (startIndex != -1) {
            String idString = url.substring(startIndex + 7);
            int endIndex = idString.indexOf("&");
            if (endIndex != -1) {
                idString = idString.substring(0, endIndex);
            }
            try {
                return Long.parseLong(idString);
            } catch (NumberFormatException e) {
                System.err.println("ID 파싱 오류: " + e.getMessage());
            }
        }
        return null;
    }

    public DetailItem detailResponse(String url) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            JSONObject jsonObject = new JSONObject(jsonResponse);

            Long id = extractIdFromUrl(url);
            if (id == null) {
                System.err.println("게임 ID 추출 오류.");
                return null;
            }

            String idString = String.valueOf(id);
            if (jsonObject.has(idString)) {
                JSONObject responseData = jsonObject.getJSONObject(idString);

                if (responseData.getBoolean("success") && responseData.has("data")) {
                    JSONObject dataJson = responseData.getJSONObject("data");
                    return parseDetail(dataJson);
                } else {
                    System.err.println("API 응답에서 데이터가 없습니다.");
                }
            } else {
                System.err.println("응답에서 " + idString + " 키를 찾을 수 없습니다.");
            }

        } catch (Exception e) {
            System.err.println("JSON 파싱 오류 또는 API 요청 오류: " + e.getMessage());
        }
        return null;
    }

    private DetailItem parseDetail(JSONObject itemJson) {


        String releaseDateString = itemJson.optString("release_date", "");
        String formattedReleaseDate = formatReleaseDate(releaseDateString);

        Map<String, String> mp4Urls = new HashMap<>();
        if (itemJson.has("mp4")) {
            JSONObject mp4Json = itemJson.getJSONObject("mp4");
            if (mp4Json.has("480")) {
                mp4Urls.put("480", mp4Json.getString("480"));
            }
            if (mp4Json.has("max")) {
                mp4Urls.put("max", mp4Json.getString("max"));
            }
        }

        List<String> developers = jsonArrayToList(itemJson.optJSONArray("developers"));
        List<String> publishers = jsonArrayToList(itemJson.optJSONArray("publishers"));
        List<String> screenshots = jsonArrayToScreenshotUrls(itemJson.optJSONArray("screenshots"));
        List<String> movies = jsonArrayToMovies(itemJson.optJSONArray("movies")); // 수정된 부분

        // HTML 태그를 제거하는 메서드 호출
        String detailedDescription = removeHtmlTags(itemJson.getString("detailed_description"));

        return new DetailItem(
                itemJson.getString("type"),
                itemJson.getString("name"),
                itemJson.getInt("required_age"),
                itemJson.getBoolean("is_free"),
                itemJson.optString("controller_support", ""),
                detailedDescription,
                itemJson.optString("short_description", ""),
                itemJson.optString("full_game_name", ""),
                itemJson.optString("header_image", ""),
                itemJson.optString("capsule_image", ""),
                itemJson.optString("website", ""),
                formattedReleaseDate,
                itemJson.optBoolean("coming_soon", false),
                itemJson.optJSONObject("support_info").optString("url", ""),
                itemJson.optJSONObject("support_info").optString("email", ""),
                itemJson.optJSONObject("content_descriptors").optString("notes", ""),
                itemJson.optJSONObject("ratings").optString("esrb", ""),
                itemJson.optJSONObject("ratings").optString("descriptors", ""),
                itemJson.optJSONObject("ratings").optInt("required_age", 0),
                developers,
                publishers,
                itemJson.optInt("initial_price", 0),
                itemJson.optInt("final_price", 0),
                itemJson.optInt("discount_percent", 0),
                itemJson.optString("background", ""),
                screenshots,
                movies,
                mp4Urls
        );
    }

    // release_date를 파싱하여 "date" 값을 추출하는 메서드
    private String formatReleaseDate(String releaseDateString) {
        try {
            JSONObject releaseDateJson = new JSONObject(releaseDateString);

            if (!releaseDateJson.getBoolean("coming_soon") && releaseDateJson.has("date")) {
                return releaseDateJson.getString("date");  // "2018년 8월 8일" 형태의 날짜 반환
            } else {
                return "날짜 정보 없음";  // coming_soon이 true인 경우 처리
            }
        } catch (Exception e) {
            System.err.println("날짜 파싱 오류: " + e.getMessage());
            return "날짜 정보 없음";
        }
    }

    // HTML 태그를 제거하는 메서드
    private String removeHtmlTags(String input) {
        if (input == null) return "";

        // 정규식을 사용하여 HTML 태그를 제거
        return input.replaceAll("<[^>]*>", "").replaceAll("&nbsp;", " ");
    }

    private List<String> jsonArrayToScreenshotUrls(JSONArray jsonArray) {
        List<String> screenshotUrls = new ArrayList<>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                String screenshotJsonString = jsonArray.optString(i);
                JSONObject screenshotJson = new JSONObject(screenshotJsonString);

                screenshotUrls.add(screenshotJson.optString("path_thumbnail"));
            }
        }
        return screenshotUrls;
    }

    private List<String> jsonArrayToMovies(JSONArray jsonArray) {
        List<String> movieUrls = new ArrayList<>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                String movieJsonString = jsonArray.optString(i);
                JSONObject movieJson = new JSONObject(movieJsonString);

                // mp4 URL을 추출
                if (movieJson.has("mp4")) {
                    JSONObject mp4Json = movieJson.getJSONObject("mp4");

                    // "max"와 "480" 해상도 URL을 모두 추출하여 리스트에 추가
                    if (mp4Json.has("480")) {
                        movieUrls.add(mp4Json.getString("480"));
                    }
                    if (mp4Json.has("max")) {
                        movieUrls.add(mp4Json.getString("max"));
                    }
                }
            }
        }
        return movieUrls;
    }

    // JSONArray를 List로 변환하는 메서드
    private List<String> jsonArrayToList(JSONArray jsonArray) {
        List<String> list = new ArrayList<>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(jsonArray.optString(i));
            }
        }
        return list;
    }
}
