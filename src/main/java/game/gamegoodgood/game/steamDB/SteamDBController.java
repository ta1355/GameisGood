package game.gamegoodgood.game.steamDB;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SteamDBController {

    private final SteamDBAPIService steamDBAPIService;

    public SteamDBController(SteamDBAPIService steamDBAPIService) {
        this.steamDBAPIService = steamDBAPIService;
    }


    @GetMapping("/test")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<GameCategoryResponse> findSteamDB() {
        GameCategoryResponse response = steamDBAPIService.findSteamAPI();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test/{id}")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<DetailItem> findDetail(@PathVariable Long id) {
        DetailItem item = steamDBAPIService.findDetail(id);
        return ResponseEntity.ok(item);
    }





}
