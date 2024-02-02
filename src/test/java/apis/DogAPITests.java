package apis;

import dev.zawarudo.holo.modules.DogAPI;
import dev.zawarudo.holo.utils.exceptions.APIException;
import dev.zawarudo.holo.utils.exceptions.InvalidRequestException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DogAPITests {

    @Test
    void testRandomImage() throws APIException {
        String url = DogAPI.getRandomImage();
        assertNotNull(url, "URL should not be null");
    }

    @Test
    void testRandomBreedImage() throws APIException, InvalidRequestException {
        String url = DogAPI.getRandomBreedImage("hound");
        assertNotNull(url, "URL should not be null");
    }

    @Test
    void testRandomSubBreedImage() throws APIException, InvalidRequestException {
        String url = DogAPI.getRandomSubBreedImage("hound", "afghan");
        assertNotNull(url, "URL should not be null");
    }

    @Test
    void testBreedImages() throws APIException, InvalidRequestException {
        List<String> images = DogAPI.getBreedImages("hound");
        assertNotNull(images, "List should not be null");
    }

    @Test
    void testSubBreedImages() throws APIException, InvalidRequestException {
        List<String> images = DogAPI.getSubBreedImages("hound", "afghan");
        assertFalse(images.isEmpty(), "List should not be empty");
    }

    @Test
    void testAllBreeds() throws APIException {
        List<DogAPI.Breed> breeds = DogAPI.getBreedList();
        assertFalse(breeds.isEmpty(), "List should not be empty");
    }

    @Test
    void testHoundSubBreeds() throws InvalidRequestException, APIException {
        List<String> subBreeds = DogAPI.getSubBreeds("hound");
        assertFalse(subBreeds.isEmpty(), "List should not be empty");
    }

    @Test
    void testHasSubBreeds() throws APIException {
        List<DogAPI.Breed> breeds = DogAPI.getBreedList();

        for (DogAPI.Breed breed : breeds) {
            // Hound has different sub-breeds
            if (breed.name().equals("hound")) {
                assertTrue(breed.hasSubBreeds(), "Breed should have sub-breeds");
            }
            // API doesn't have any Chihuahua sub-breeds
            if (breed.name().equals("chihuahua")) {
                assertFalse(breed.hasSubBreeds(), "Breed should not have sub-breeds");
            }
        }
    }

    @Test
    void testInvalidBreed() {
        assertThrows(InvalidRequestException.class, () -> DogAPI.getSubBreeds("nonsense"), "Method should throw an exception");
        assertThrows(InvalidRequestException.class, () -> DogAPI.getRandomBreedImage("nonsense"), "Method should throw an exception");
        assertThrows(InvalidRequestException.class, () -> DogAPI.getBreedImages("nonsense"), "Method should throw an exception");
    }

    @Test
    void testInvalidSubBreed() {
        assertThrows(InvalidRequestException.class, () -> DogAPI.getRandomSubBreedImage("hound", "nonsense"), "Method should throw an exception");
        assertThrows(InvalidRequestException.class, () -> DogAPI.getRandomSubBreedImage("nonsense", "afghan"), "Method should throw an exception");

        assertThrows(InvalidRequestException.class, () -> DogAPI.getSubBreedImages("hound", "nonsense"), "Method should throw an exception");
        assertThrows(InvalidRequestException.class, () -> DogAPI.getSubBreedImages("nonsense", "afghan"), "Method should throw an exception");
    }
}