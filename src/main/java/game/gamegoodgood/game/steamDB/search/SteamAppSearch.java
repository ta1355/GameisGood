package game.gamegoodgood.game.steamDB.search;

public class SteamAppSearch {

    private String appid;
    private String name;
    private String icon;
    private String logo;

    public SteamAppSearch() {
    }

    public SteamAppSearch(String appid, String name, String icon, String logo) {
        this.appid = appid;
        this.name = name;
        this.icon = icon;
        this.logo = logo;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
