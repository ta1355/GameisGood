package game.gamegoodgood.game;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {

    private  final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/findgame")
    public GameDto findGame() {
        GameDto gameData = gameService.find();
        return gameData; // JSON 형태로 자동 변환됨
    }


}
