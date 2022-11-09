package url.storage;

import java.util.HashMap;
import java.util.Map;

public class InMemoryStorage implements Storage {
    private Map<String, String> storageMap;

    public InMemoryStorage() {
        this.storageMap = new HashMap<>();
        this.storageMap.put("test", "https://www.google.com");
    }

    @Override
    public String getItem(String key) {
        return this.storageMap.get(key);
    }

    @Override
    public void putItem(String shortUrl, String longUrl) {
        this.storageMap.put(shortUrl, longUrl);
    }
}
