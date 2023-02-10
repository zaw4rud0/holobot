package apis;

import dev.zawarudo.holo.apis.xkcd.XkcdAPI;
import dev.zawarudo.holo.apis.xkcd.XkcdComic;
import dev.zawarudo.holo.exceptions.APIException;
import dev.zawarudo.holo.exceptions.InvalidRequestException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class XkcdTests {

    @Test
    public void testLatest() throws APIException {
        XkcdComic latest = XkcdAPI.getLatest();
        assertNotNull(latest);
        assertNotNull(latest.getTitle());
        assertNotEquals(0, latest.getIssueNr());
    }

    @Test
    public void testNum() throws APIException, InvalidRequestException {
        XkcdComic comic = XkcdAPI.getComic(1234);
        assertNotNull(comic);
        assertNotNull(comic.getTitle());
        assertNotEquals(0, comic.getIssueNr());
        assertEquals("5/7/2013", comic.getDate());
    }

    @Test
    public void testInvalidNum() {
        assertThrows(InvalidRequestException.class, () -> XkcdAPI.getComic(-1));
        assertThrows(InvalidRequestException.class, () -> XkcdAPI.getComic(10000));
    }
}