package jp.speakbuddy.audiovault.model;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class PhraseTest {

    @Test
    void testGettersAndSetters() {
        // Arrange
        Phrase phrase = new Phrase();
        phrase.setId(1L);
        phrase.setUserId(100L);

        // Act & Assert
        assertEquals(1L, phrase.getId());
        assertEquals(100L, phrase.getUserId());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        Phrase phrase1 = new Phrase();
        phrase1.setId(1L);
        phrase1.setUserId(100L);

        Phrase phrase2 = new Phrase();
        phrase2.setId(1L);
        phrase2.setUserId(100L);

        Phrase phrase3 = new Phrase();
        phrase3.setId(2L);
        phrase3.setUserId(200L);

        // Act & Assert
        assertEquals(phrase1, phrase2); // Same data should be equal
        assertNotEquals(phrase1, phrase3); // Different data should not be equal
        assertEquals(phrase1.hashCode(), phrase2.hashCode()); // Equal objects should have the same hashcode
        assertNotEquals(phrase1.hashCode(), phrase3.hashCode()); // Different objects should have different hashcodes
    }

    @Test
    void testToString() {
        // Arrange
        Phrase phrase = new Phrase();
        phrase.setId(1L);
        phrase.setUserId(100L);

        // Act
        String result = phrase.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("userId=100"));
    }

    @Test
    void testCanEqual() {
        // Arrange
        Phrase phrase1 = new Phrase();
        Phrase phrase2 = new Phrase();

        // Act & Assert
        assertTrue(phrase1.canEqual(phrase2)); // Two instances of the same class should be equal
        assertFalse(phrase1.canEqual(new Object())); // Different class should not be equal
    }
}
