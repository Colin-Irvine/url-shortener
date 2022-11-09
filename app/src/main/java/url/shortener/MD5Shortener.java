package url.shortener;

import url.storage.Storage;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MD5Shortener implements Shortener {
    private Storage storage;
    private MessageDigest messageDigest;

    public MD5Shortener(Storage storage) throws NoSuchAlgorithmException {
        this.storage = storage;
        this.messageDigest = MessageDigest.getInstance("MD5");
    }

    @Override
    public String getLongName(String key) {
        return storage.getItem(key);
    }

    @Override
    public String getShortName(String url) {
        messageDigest.update(url.getBytes(StandardCharsets.UTF_8));
        byte[] shortUrlBytes = messageDigest.digest();
        String base36Url = new BigInteger(1, shortUrlBytes).toString(36);
        storage.putItem(base36Url, url);

        return base36Url;
    }
}
