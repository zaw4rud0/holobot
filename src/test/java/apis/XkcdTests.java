package apis;

import com.xharlock.holo.apis.XkcdAPI;
import com.xharlock.holo.exceptions.APIException;
import com.xharlock.holo.exceptions.InvalidRequestException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class XkcdTests {

    @Test
    public void testLatest() throws APIException {
        XkcdAPI.Comic latest = XkcdAPI.getLatest();
        assertNotNull(latest);
        assertNotNull(latest.getTitle());
        assertNotEquals(0, latest.getIssueNr());
        System.out.println(latest);
    }

    @Test
    public void testNum() throws APIException, InvalidRequestException {
        XkcdAPI.Comic comic = XkcdAPI.getComic(1234);
        assertNotNull(comic);
        assertNotNull(comic.getTitle());
        assertNotEquals(0, comic.getIssueNr());
        System.out.println(comic);
    }

    @Test
    public void testInvalidNum() {
        assertThrows(InvalidRequestException.class, () -> XkcdAPI.getComic(-1));
        assertThrows(InvalidRequestException.class, () -> XkcdAPI.getComic(10000));
    }
}