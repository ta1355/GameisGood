package game.gamegoodgood.game.steamDB;


import game.gamegoodgood.game.steamDB.detail.DetailItem;
import game.gamegoodgood.game.steamDB.detail.DetailParser;
import game.gamegoodgood.game.steamDB.search.SteamAppSearch;
import game.gamegoodgood.game.steamDB.search.SteamAppSearchParser;
import game.gamegoodgood.game.steamDB.special.GameCategoryResponse;
import game.gamegoodgood.game.steamDB.special.SpecialsParser;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class SteamDBAPIService {
    private final SpecialsParser specialsParser = new SpecialsParser();
    private final DetailParser detailParser = new DetailParser();
    private final SteamAppSearchParser steamAppSearchParser = new SteamAppSearchParser();

    public GameCategoryResponse findSpecial() {
        String url = "https://store.steampowered.com/api/featuredcategories?cc=KR&l=korean";
        return specialsParser.parse(url);
    }

    public DetailItem findDetail(Long id) {
        String url = "https://store.steampowered.com/api/appdetails?appids="+id+"&cc=KR&l=korean";
        System.out.println("url 확인용: " +url);
        return detailParser.detailResponse(url);
    }

    public List<SteamAppSearch> steamAppSearch(String search) {
        String url = "https://steamcommunity.com/actions/SearchApps/" + search;
        System.out.println("url 확인용: " + url);
        return steamAppSearchParser.searchResponse(url);
    }
}