package game.gamegoodgood.game.steamDB;

import game.gamegoodgood.game.steamDB.detail.DetailItem;
import game.gamegoodgood.game.steamDB.detail.DetailParser;
import game.gamegoodgood.game.steamDB.search.SteamAppSearch;
import game.gamegoodgood.game.steamDB.search.SteamAppSearchParser;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class SteamDBAPIService {
    private final String featuredCategoriesUrl = "https://store.steampowered.com/api/featuredcategories?cc=KR&l=korean";
    private final String appDetailsUrl = "https://store.steampowered.com/api/appdetails?appids=%s&cc=KR&l=korean";
    private final String searchUrl = "https://steamcommunity.com/actions/SearchApps/";
    private final GenericSteamParser parser;
    private final RestTemplate restTemplate;
    private final DetailParser detailParser;
    private final SteamAppSearchParser searchParser;

    public SteamDBAPIService(GenericSteamParser parser, RestTemplate restTemplate,
                             DetailParser detailParser, SteamAppSearchParser searchParser) {
        this.parser = parser;
        this.restTemplate = restTemplate;
        this.detailParser = detailParser;
        this.searchParser = searchParser;
    }

    public GameCategoryResponse findSpecial() {
        String jsonResponse = fetchDataFromApi(featuredCategoriesUrl);
        return parser.parseCategory(jsonResponse, "specials");
    }

    public GameCategoryResponse findTopSellers() {
        String jsonResponse = fetchDataFromApi(featuredCategoriesUrl);
        return parser.parseCategory(jsonResponse, "top_sellers");
    }

    public GameCategoryResponse findComingSoon() {
        String jsonResponse = fetchDataFromApi(featuredCategoriesUrl);
        return parser.parseCategory(jsonResponse, "coming_soon");
    }

    public GameCategoryResponse findNewReleases() {
        String jsonResponse = fetchDataFromApi(featuredCategoriesUrl);
        return parser.parseCategory(jsonResponse, "new_releases");
    }

    public DetailItem findDetail(Long id) {
        String url = String.format(appDetailsUrl, id);
        return detailParser.detailResponse(url);
    }

    public List<SteamAppSearch> steamAppSearch(String search) {
        String url = searchUrl + search;
        return searchParser.searchResponse(url);
    }

    private String fetchDataFromApi(String url) {
        return restTemplate.getForObject(url, String.class);
    }
}