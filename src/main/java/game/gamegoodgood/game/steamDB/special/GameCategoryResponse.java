package game.gamegoodgood.game.steamDB.special;

import java.util.List;

public class GameCategoryResponse {


    private List<SteamItem> specials;


    public GameCategoryResponse(List<SteamItem> specials) {
        this.specials = specials;
    }

    public List<SteamItem> getSpecials() {
        return specials;
    }

    public void setSpecials(List<SteamItem> specials) {
        this.specials = specials;
    }

}