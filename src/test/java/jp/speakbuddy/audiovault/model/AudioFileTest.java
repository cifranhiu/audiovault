package jp.speakbuddy.audiovault.model;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class AudioFileTest {

    @Test
    void testGettersAndSetters() {
        // Arrange
        AudioFile audioFile = new AudioFile();
        audioFile.setId(1L);
        audioFile.setUserId(100L);
        audioFile.setPhraseId(200L);
        audioFile.setM4aFilepath("path/to/audio.m4a");
        audioFile.setWavFilepath("path/to/audio.wav");

        // Act & Assert
        assertEquals(1L, audioFile.getId());
        assertEquals(100L, audioFile.getUserId());
        assertEquals(200L, audioFile.getPhraseId());
        assertEquals("path/to/audio.m4a", audioFile.getM4aFilepath());
        assertEquals("path/to/audio.wav", audioFile.getWavFilepath());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        AudioFile file1 = new AudioFile();
        file1.setId(1L);
        file1.setUserId(100L);
        file1.setPhraseId(200L);
        file1.setM4aFilepath("path/to/audio.m4a");
        file1.setWavFilepath("path/to/audio.wav");

        AudioFile file2 = new AudioFile();
        file2.setId(1L);
        file2.setUserId(100L);
        file2.setPhraseId(200L);
        file2.setM4aFilepath("path/to/audio.m4a");
        file2.setWavFilepath("path/to/audio.wav");

        AudioFile file3 = new AudioFile();
        file3.setId(2L);
        file3.setUserId(101L);
        file3.setPhraseId(201L);
        file3.setM4aFilepath("path/to/another.m4a");
        file3.setWavFilepath("path/to/another.wav");

        // Act & Assert
        assertEquals(file1, file2); // Same data should be equal
        assertNotEquals(file1, file3); // Different data should not be equal
        assertEquals(file1.hashCode(), file2.hashCode()); // Equal objects should have the same hashcode
        assertNotEquals(file1.hashCode(), file3.hashCode()); // Different objects should have different hashcodes
    }

    @Test
    void testToString() {
        // Arrange
        AudioFile file = new AudioFile();
        file.setId(1L);
        file.setUserId(100L);
        file.setPhraseId(200L);
        file.setM4aFilepath("path/to/audio.m4a");
        file.setWavFilepath("path/to/audio.wav");

        // Act
        String result = file.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("userId=100"));
        assertTrue(result.contains("phraseId=200"));
        assertTrue(result.contains("m4aFilepath=path/to/audio.m4a"));
        assertTrue(result.contains("wavFilepath=path/to/audio.wav"));
    }

    @Test
    void testCanEqual() {
        // Arrange
        AudioFile file1 = new AudioFile();
        AudioFile file2 = new AudioFile();

        // Act & Assert
        assertTrue(file1.canEqual(file2)); // Two instances of the same class should be equal
        assertFalse(file1.canEqual(new Object())); // Different class should not be equal
    }
}
