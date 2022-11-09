package url.shortener;


public interface Shortener {
    String getLongName(String key);
    String getShortName(String url);
}
