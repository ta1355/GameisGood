package game.gamegoodgood.game.steamDB;

import game.gamegoodgood.game.steamDB.detail.DetailItem;
import game.gamegoodgood.game.steamDB.search.SteamAppSearch;
import game.gamegoodgood.game.steamDB.special.GameCategoryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SteamDBController {

    private final SteamDBAPIService steamDBAPIService;

    public SteamDBController(SteamDBAPIService steamDBAPIService) {
        this.steamDBAPIService = steamDBAPIService;
    }


    @GetMapping("/test")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<GameCategoryResponse> findSteamDB() {
        GameCategoryResponse response = steamDBAPIService.findSpecial();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test/{id}")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<DetailItem> findDetail(@PathVariable Long id) {
        DetailItem item = steamDBAPIService.findDetail(id);
        return ResponseEntity.ok(item);
    }

    @GetMapping("/search/{search}")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<List<SteamAppSearch>> searchApp(@PathVariable String search) {
        List<SteamAppSearch> steamAppSearchList = steamDBAPIService.steamAppSearch(search);

        if (steamAppSearchList == null || steamAppSearchList.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }

        return ResponseEntity.ok(steamAppSearchList);
    }




}
