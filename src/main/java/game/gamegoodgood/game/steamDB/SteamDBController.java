package game.gamegoodgood.game.steamDB;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SteamDBController {

    private final SteamDBAPIService steamDBAPIService;

    public SteamDBController(SteamDBAPIService steamDBAPIService) {
        this.steamDBAPIService = steamDBAPIService;
    }


    @GetMapping("/test")
    public ResponseEntity<GameCategoryResponse> findSteamDB() {
        GameCategoryResponse response = steamDBAPIService.findSteamAPI();
        return ResponseEntity.ok(response);
    }




}
