package apis;

import dev.zawarudo.holo.apis.DogAPI;
import dev.zawarudo.holo.exceptions.APIException;
import dev.zawarudo.holo.exceptions.InvalidRequestException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DogAPITests {

    @Test
    void testRandomImage() throws APIException {
        String url = DogAPI.getRandomImage();
        assertNotNull(url);
    }

    @Test
    void testRandomBreedImage() throws APIException, InvalidRequestException {
        String url = DogAPI.getRandomBreedImage("hound");
        assertNotNull(url);
    }

    @Test
    void testRandomSubBreedImage() throws APIException, InvalidRequestException {
        String url = DogAPI.getRandomSubBreedImage("hound", "afghan");
        assertNotNull(url);
    }

    @Test
    void testBreedImages() throws APIException, InvalidRequestException {
        List<String> images = DogAPI.getBreedImages("hound");
        assertNotNull(images);
    }

    @Test
    void testSubBreedImages() throws APIException, InvalidRequestException {
        List<String> images = DogAPI.getSubBreedImages("hound", "afghan");
        assertNotNull(images);
    }

    @Test
    void testAllBreeds() throws APIException {
        List<DogAPI.Breed> breeds = DogAPI.getBreedList();

        assertNotNull(breeds);
        assertFalse(breeds.isEmpty());
    }

    @Test
    void testHoundSubBreeds() throws InvalidRequestException, APIException {
        List<String> subBreeds = DogAPI.getSubBreeds("hound");

        assertNotNull(subBreeds);
        assertFalse(subBreeds.isEmpty());
        assertTrue(subBreeds.contains("afghan"));
    }

    @Test
    void testHasSubBreeds() throws APIException {
        List<DogAPI.Breed> breeds = DogAPI.getBreedList();

        for (DogAPI.Breed breed : breeds) {
            // Hound has different sub-breeds
            if (breed.name().equals("hound")) {
                assertTrue(breed.hasSubBreeds());
            }
            // API doesn't have any Chihuahua sub-breeds
            if (breed.name().equals("chihuahua")) {
                assertFalse(breed.hasSubBreeds());
            }
        }
    }

    @Test
    void testInvalidBreed() {
        assertThrows(InvalidRequestException.class, () -> DogAPI.getSubBreeds("nonsense"));
        assertThrows(InvalidRequestException.class, () -> DogAPI.getRandomBreedImage("nonsense"));
        assertThrows(InvalidRequestException.class, () -> DogAPI.getBreedImages("nonsense"));
    }

    @Test
    void testInvalidSubBreed() {
        assertThrows(InvalidRequestException.class, () -> DogAPI.getRandomSubBreedImage("hound", "nonsense"));
        assertThrows(InvalidRequestException.class, () -> DogAPI.getRandomSubBreedImage("nonsense", "afghan"));

        assertThrows(InvalidRequestException.class, () -> DogAPI.getSubBreedImages("hound", "nonsense"));
        assertThrows(InvalidRequestException.class, () -> DogAPI.getSubBreedImages("nonsense", "afghan"));
    }
}