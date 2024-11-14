package game.gamegoodgood.game.steamDB.special;

public class SpecialItem {
    private int id;
    private String name;
    private boolean discounted;
    private int discountPercent;
    private int originalPrice;
    private int finalPrice;
    private String currency;
    private String largeCapsuleImage;
    private String smallCapsuleImage;
    private boolean windowsAvailable;
    private boolean macAvailable;
    private boolean linuxAvailable;
    private boolean streamingVideoAvailable;
    private long discountExpiration;
    private String headerImage;
    private String headline;

    public SpecialItem() {
    }

    public SpecialItem(int id, String name, boolean discounted, int discountPercent, int originalPrice, int finalPrice, String currency, String largeCapsuleImage, String smallCapsuleImage, boolean windowsAvailable, boolean macAvailable, boolean linuxAvailable, boolean streamingVideoAvailable, long discountExpiration, String headerImage, String headline) {
        this.id = id;
        this.name = name;
        this.discounted = discounted;
        this.discountPercent = discountPercent;
        this.originalPrice = originalPrice;
        this.finalPrice = finalPrice;
        this.currency = currency;
        this.largeCapsuleImage = largeCapsuleImage;
        this.smallCapsuleImage = smallCapsuleImage;
        this.windowsAvailable = windowsAvailable;
        this.macAvailable = macAvailable;
        this.linuxAvailable = linuxAvailable;
        this.streamingVideoAvailable = streamingVideoAvailable;
        this.discountExpiration = discountExpiration;
        this.headerImage = headerImage;
        this.headline = headline;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDiscounted() {
        return discounted;
    }

    public void setDiscounted(boolean discounted) {
        this.discounted = discounted;
    }

    public int getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(int discountPercent) {
        this.discountPercent = discountPercent;
    }

    public int getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(int originalPrice) {
        this.originalPrice = originalPrice;
    }

    public int getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(int finalPrice) {
        this.finalPrice = finalPrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getLargeCapsuleImage() {
        return largeCapsuleImage;
    }

    public void setLargeCapsuleImage(String largeCapsuleImage) {
        this.largeCapsuleImage = largeCapsuleImage;
    }

    public String getSmallCapsuleImage() {
        return smallCapsuleImage;
    }

    public void setSmallCapsuleImage(String smallCapsuleImage) {
        this.smallCapsuleImage = smallCapsuleImage;
    }

    public boolean isWindowsAvailable() {
        return windowsAvailable;
    }

    public void setWindowsAvailable(boolean windowsAvailable) {
        this.windowsAvailable = windowsAvailable;
    }

    public boolean isMacAvailable() {
        return macAvailable;
    }

    public void setMacAvailable(boolean macAvailable) {
        this.macAvailable = macAvailable;
    }

    public boolean isLinuxAvailable() {
        return linuxAvailable;
    }

    public void setLinuxAvailable(boolean linuxAvailable) {
        this.linuxAvailable = linuxAvailable;
    }

    public boolean isStreamingVideoAvailable() {
        return streamingVideoAvailable;
    }

    public void setStreamingVideoAvailable(boolean streamingVideoAvailable) {
        this.streamingVideoAvailable = streamingVideoAvailable;
    }

    public long getDiscountExpiration() {
        return discountExpiration;
    }

    public void setDiscountExpiration(long discountExpiration) {
        this.discountExpiration = discountExpiration;
    }

    public String getHeaderImage() {
        return headerImage;
    }

    public void setHeaderImage(String headerImage) {
        this.headerImage = headerImage;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getFormattedOriginalPrice() {
        return String.format("%,d원", originalPrice / 100);
    }

    public String getFormattedFinalPrice() {
        return String.format("%,d원", finalPrice / 100);
    }

}
