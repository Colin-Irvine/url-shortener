package url.storage;


public interface Storage {
    String getItem(String key);
    void putItem(String shortUrl, String longUrl);
}
