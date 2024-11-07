package game.gamegoodgood.game.steamDB;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

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