package jp.speakbuddy.audiovault.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import jp.speakbuddy.audiovault.model.AudioFile;

@SuppressWarnings("unused")
class AudioFileDTOTest {
    private Exception assertThrows;

    @Test
    void testFromMethod() {
        // Arrange
        AudioFile audioFile = new AudioFile();
        audioFile.setWavFilepath("test-path/audio.wav");
        audioFile.setM4aFilepath("test-path/audio.m4a");

        // Act
        AudioFileDTO dto = AudioFileDTO.from(audioFile);

        // Assert
        assertNotNull(dto);
        assertEquals("test-path/audio.wav", dto.getWavFile());
        assertEquals("test-path/audio.m4a", dto.getM4aFile());
    }

    @Test
    void testFromMethod_WithNullAudioFile() {
        // Act & Assert
        assertThrows = assertThrows(NullPointerException.class, () -> AudioFileDTO.from(null));
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        AudioFileDTO dto1 = new AudioFileDTO();
        dto1.setWavFile("test-path/audio1.wav");
        dto1.setM4aFile("test-path/audio1.m4a");

        AudioFileDTO dto2 = new AudioFileDTO();
        dto2.setWavFile("test-path/audio1.wav");
        dto2.setM4aFile("test-path/audio1.m4a");

        AudioFileDTO dto3 = new AudioFileDTO();
        dto3.setWavFile("test-path/audio2.wav");
        dto3.setM4aFile("test-path/audio2.m4a");

        // Act & Assert
        assertEquals(dto1, dto2); // Same content should be equal
        assertNotEquals(dto1, dto3); // Different content should not be equal
        assertEquals(dto1.hashCode(), dto2.hashCode()); // Hash codes should match for equal objects
        assertNotEquals(dto1.hashCode(), dto3.hashCode()); // Hash codes should be different for different objects
    }

    @Test
    void testToString() {
        // Arrange
        AudioFileDTO dto = new AudioFileDTO();
        dto.setWavFile("test-path/audio.wav");
        dto.setM4aFile("test-path/audio.m4a");

        // Act
        String result = dto.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("wavFile=test-path/audio.wav"));
        assertTrue(result.contains("m4aFile=test-path/audio.m4a"));
    }

    @Test
    void testCanEqual() {
        // Arrange
        AudioFileDTO dto1 = new AudioFileDTO();
        AudioFileDTO dto2 = new AudioFileDTO();

        // Act & Assert
        assertTrue(dto1.canEqual(dto2)); // Two instances of the same class should be equal
        assertFalse(dto1.canEqual(new Object())); // Different class should not be equal
    }
}
