package entity;

public class FilterSettings {
    private String roomType; // e.g. "2‑Room", "3‑Room" or null = any
    private Integer minPrice; // null = no minimum
    private Integer maxPrice; // null = no maximum

    public FilterSettings(String roomType, Integer minPrice, Integer maxPrice) {
        this.roomType = roomType;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    // getters...
    public String getRoomType() {
        return roomType;
    }

    public Integer getMinPrice() {
        return minPrice;
    }

    public Integer getMaxPrice() {
        return maxPrice;
    }

    /** serialize to CSV column */
    /**
     * The `toCsv` function returns a CSV formatted string with room type, minimum price, and maximum
     * price values.
     * 
     * @return A CSV (Comma-Separated Values) string is being returned, with the room type, minimum
     * price, and maximum price values formatted as a comma-separated list. If any of these values are
     * null, an empty string is used instead.
     */
    public String toCsv() {
        return String.format("%s,%s,%s",
                roomType == null ? "" : roomType,
                minPrice == null ? "" : minPrice,
                maxPrice == null ? "" : maxPrice);
    }

    /** parse from raw CSV string (from userlist CSV) */
    /**
     * The `fromCsv` function parses a CSV string to create a `FilterSettings` object with room,
     * minimum, and maximum values.
     * 
     * @param raw The `fromCsv` method takes a raw CSV string as input and parses it to create a
     * `FilterSettings` object. The CSV string should contain three values separated by commas
     * representing room, minimum value, and maximum value respectively. If the input string is null or
     * empty, it returns a new `
     * @return The `fromCsv` method returns a `FilterSettings` object created using the values
     * extracted from the CSV string `raw`. If the `raw` string is null or blank, a new
     * `FilterSettings` object with all fields set to null is returned. Otherwise, the method splits
     * the `raw` string by commas and extracts values for room, minimum, and maximum from the parts
     * array. These
     */
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