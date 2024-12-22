package game.gamegoodgood.game.steamDB;

import game.gamegoodgood.game.steamDB.detail.DetailItem;
import game.gamegoodgood.game.steamDB.search.SteamAppSearch;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SteamDBController {

    private final SteamDBAPIService steamDBAPIService;

    public SteamDBController(SteamDBAPIService steamDBAPIService) {
        this.steamDBAPIService = steamDBAPIService;
    }


    @GetMapping("/game/specials")
    public ResponseEntity<GameCategoryResponse> findSpecials() {
        GameCategoryResponse response = steamDBAPIService.findSpecial();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/game/top_sellers")
    public ResponseEntity<GameCategoryResponse> findTopSellers() {
        GameCategoryResponse response = steamDBAPIService.findTopSellers();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/game/coming_soon")
    public ResponseEntity<GameCategoryResponse> findComingSoon() {
        GameCategoryResponse response = steamDBAPIService.findComingSoon();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/game/new_releases")
    public ResponseEntity<GameCategoryResponse> findNewReleases() {
        GameCategoryResponse response = steamDBAPIService.findNewReleases();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/game/{id}")
    public ResponseEntity<DetailItem> findDetail(@PathVariable Long id) {
        DetailItem item = steamDBAPIService.findDetail(id);
        return ResponseEntity.ok(item);
    }

    @GetMapping("/search/{search}")
    public ResponseEntity<List<SteamAppSearch>> searchApp(@PathVariable String search) {
        List<SteamAppSearch> steamAppSearchList = steamDBAPIService.steamAppSearch(search);

        if (steamAppSearchList == null || steamAppSearchList.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }

        return ResponseEntity.ok(steamAppSearchList);
    }




}
