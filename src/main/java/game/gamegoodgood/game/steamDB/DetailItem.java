package game.gamegoodgood.game.steamDB;

import java.util.List;
import java.util.Map;

public class DetailItem {

    private String type;
    private String name;
    private int requiredAge;
    private boolean isFree;
    private String controllerSupport;
    private String detailedDescription;
    private String shortDescription;
    private String fullGameName;
    private String headerImage;
    private String capsuleImage;
    private String website;
    private String releaseDate;
    private boolean comingSoon;
    private String supportUrl;
    private String supportEmail;
    private String contentNotes;
    private String esrbRating;
    private String esrbDescriptors;
    private int esrbRequiredAge;
    private List<String> developers;
    private List<String> publishers;
    private int initialPrice;
    private int finalPrice;
    private int discountPercent;
    private String background;
    private List<String> screenshots;
    private List<String> movies;
    private Map<String, String> mp4Urls;

    protected DetailItem() {
    }

    public DetailItem(String type, String name, int requiredAge, boolean isFree, String controllerSupport, String detailedDescription, String shortDescription, String fullGameName, String headerImage, String capsuleImage, String website, String releaseDate, boolean comingSoon, String supportUrl, String supportEmail, String contentNotes, String esrbRating, String esrbDescriptors, int esrbRequiredAge, List<String> developers, List<String> publishers, int initialPrice, int finalPrice, int discountPercent, String background, List<String> screenshots, List<String> movies, Map<String, String> mp4Urls) {
        this.type = type;
        this.name = name;
        this.requiredAge = requiredAge;
        this.isFree = isFree;
        this.controllerSupport = controllerSupport;
        this.detailedDescription = detailedDescription;
        this.shortDescription = shortDescription;
        this.fullGameName = fullGameName;
        this.headerImage = headerImage;
        this.capsuleImage = capsuleImage;
        this.website = website;
        this.releaseDate = releaseDate;
        this.comingSoon = comingSoon;
        this.supportUrl = supportUrl;
        this.supportEmail = supportEmail;
        this.contentNotes = contentNotes;
        this.esrbRating = esrbRating;
        this.esrbDescriptors = esrbDescriptors;
        this.esrbRequiredAge = esrbRequiredAge;
        this.developers = developers;
        this.publishers = publishers;
        this.initialPrice = initialPrice;
        this.finalPrice = finalPrice;
        this.discountPercent = discountPercent;
        this.background = background;
        this.screenshots = screenshots;
        this.movies = movies;
        this.mp4Urls = mp4Urls;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRequiredAge() {
        return requiredAge;
    }

    public void setRequiredAge(int requiredAge) {
        this.requiredAge = requiredAge;
    }

    public boolean isFree() {
        return isFree;
    }

    public void setFree(boolean free) {
        isFree = free;
    }

    public String getControllerSupport() {
        return controllerSupport;
    }

    public void setControllerSupport(String controllerSupport) {
        this.controllerSupport = controllerSupport;
    }

    public String getDetailedDescription() {
        return detailedDescription;
    }

    public void setDetailedDescription(String detailedDescription) {
        this.detailedDescription = detailedDescription;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getFullGameName() {
        return fullGameName;
    }

    public void setFullGameName(String fullGameName) {
        this.fullGameName = fullGameName;
    }

    public String getHeaderImage() {
        return headerImage;
    }

    public void setHeaderImage(String headerImage) {
        this.headerImage = headerImage;
    }

    public String getCapsuleImage() {
        return capsuleImage;
    }

    public void setCapsuleImage(String capsuleImage) {
        this.capsuleImage = capsuleImage;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public boolean isComingSoon() {
        return comingSoon;
    }

    public void setComingSoon(boolean comingSoon) {
        this.comingSoon = comingSoon;
    }

    public String getSupportUrl() {
        return supportUrl;
    }

    public void setSupportUrl(String supportUrl) {
        this.supportUrl = supportUrl;
    }

    public String getSupportEmail() {
        return supportEmail;
    }

    public void setSupportEmail(String supportEmail) {
        this.supportEmail = supportEmail;
    }

    public String getContentNotes() {
        return contentNotes;
    }

    public void setContentNotes(String contentNotes) {
        this.contentNotes = contentNotes;
    }

    public String getEsrbRating() {
        return esrbRating;
    }

    public void setEsrbRating(String esrbRating) {
        this.esrbRating = esrbRating;
    }

    public String getEsrbDescriptors() {
        return esrbDescriptors;
    }

    public void setEsrbDescriptors(String esrbDescriptors) {
        this.esrbDescriptors = esrbDescriptors;
    }

    public int getEsrbRequiredAge() {
        return esrbRequiredAge;
    }

    public void setEsrbRequiredAge(int esrbRequiredAge) {
        this.esrbRequiredAge = esrbRequiredAge;
    }

    public List<String> getDevelopers() {
        return developers;
    }

    public void setDevelopers(List<String> developers) {
        this.developers = developers;
    }

    public List<String> getPublishers() {
        return publishers;
    }

    public void setPublishers(List<String> publishers) {
        this.publishers = publishers;
    }

    public int getInitialPrice() {
        return initialPrice;
    }

    public void setInitialPrice(int initialPrice) {
        this.initialPrice = initialPrice;
    }

    public int getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(int finalPrice) {
        this.finalPrice = finalPrice;
    }

    public int getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(int discountPercent) {
        this.discountPercent = discountPercent;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public List<String> getScreenshots() {
        return screenshots;
    }

    public void setScreenshots(List<String> screenshots) {
        this.screenshots = screenshots;
    }

    public List<String> getMovies() {
        return movies;
    }

    public void setMovies(List<String> movies) {
        this.movies = movies;
    }

    public Map<String, String> getMp4Urls() {
        return mp4Urls;
    }

    public void setMp4Urls(Map<String, String> mp4Urls) {
        this.mp4Urls = mp4Urls;
    }
}
