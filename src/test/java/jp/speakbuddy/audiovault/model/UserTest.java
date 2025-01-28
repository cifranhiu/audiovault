package jp.speakbuddy.audiovault.model;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    void testGettersAndSetters() {
        // Arrange
        User user = new User();
        user.setId(1L);

        // Act & Assert
        assertEquals(1L, user.getId());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        User user1 = new User();
        user1.setId(1L);

        User user2 = new User();
        user2.setId(1L);

        User user3 = new User();
        user3.setId(2L);

        // Act & Assert
        assertEquals(user1, user2); // Same data should be equal
        assertNotEquals(user1, user3); // Different data should not be equal
        assertEquals(user1.hashCode(), user2.hashCode()); // Equal objects should have the same hashcode
        assertNotEquals(user1.hashCode(), user3.hashCode()); // Different objects should have different hashcodes
    }

    @Test
    void testToString() {
        // Arrange
        User user = new User();
        user.setId(1L);

        // Act
        String result = user.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("id=1"));
    }

    @Test
    void testCanEqual() {
        // Arrange
        User user1 = new User();
        User user2 = new User();

        // Act & Assert
        assertTrue(user1.canEqual(user2)); // Two instances of the same class should be equal
        assertFalse(user1.canEqual(new Object())); // Different class should not be equal
    }
}
