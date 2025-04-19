package entity;

public class FilterSettings {
    private String roomType;    // e.g. "2‑Room", "3‑Room" or null = any
    private Integer minPrice;   // null = no minimum
    private Integer maxPrice;   // null = no maximum

    public FilterSettings(String roomType, Integer minPrice, Integer maxPrice) {
        this.roomType = roomType;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }
    
    // getters...
    public String getRoomType()   { return roomType; }
    public Integer getMinPrice()  { return minPrice; }
    public Integer getMaxPrice()  { return maxPrice; }

    /** serialize to CSV column */
    public String toCsv() {
        return String.format("%s,%s,%s",
            roomType == null ? "" : roomType,
            minPrice == null ? "" : minPrice,
            maxPrice == null ? "" : maxPrice);
    }
    
    /** parse from raw CSV string (from userlist CSV) */
    public static FilterSettings fromCsv(String raw) {
        if (raw == null || raw.isBlank()) {
            return new FilterSettings(null, null, null);
        }
        String[] parts = raw.split(",", -1);
        String room = (parts.length > 0 && !parts[0].isBlank()) ? parts[0] : null;
        Integer mn = (parts.length > 1 && !parts[1].isBlank()) ? Integer.valueOf(parts[1]) : null;
        Integer mx = (parts.length > 2 && !parts[2].isBlank()) ? Integer.valueOf(parts[2]) : null;
        return new FilterSettings(room, mn, mx);
    }
}