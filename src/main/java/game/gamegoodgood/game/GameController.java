package game.gamegoodgood.game;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {

    private  final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/game")
    public GameDto game(
            @RequestParam(value = "display", defaultValue = "10") String display,
            @RequestParam(value = "pageno", defaultValue = "1") String pageNo,
            @RequestParam(value = "gametitle", defaultValue = "") String gametitle,
            @RequestParam(value = "entname", defaultValue = "") String entname,
            @RequestParam(value = "rateno", defaultValue = "") String rateno) {

        // 서비스 호출하여 DTO 반환
        return gameService.find(display, pageNo, gametitle, entname, rateno);
    }


}
