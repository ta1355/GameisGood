package game.gamegoodgood.game.steamDB.special;

import java.util.List;

public class GameCategoryResponse {


    private List<SpecialItem> specials;


    public GameCategoryResponse(List<SpecialItem> specials) {
        this.specials = specials;
    }

    public List<SpecialItem> getSpecials() {
        return specials;
    }

    public void setSpecials(List<SpecialItem> specials) {
        this.specials = specials;
    }

}