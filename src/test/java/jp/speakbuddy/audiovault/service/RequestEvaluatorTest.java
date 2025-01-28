package jp.speakbuddy.audiovault.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import jp.speakbuddy.audiovault.exception.ResourceNotFoundException;
import jp.speakbuddy.audiovault.exception.ValidationException;
import jp.speakbuddy.audiovault.model.Phrase;
import jp.speakbuddy.audiovault.model.User;
import jp.speakbuddy.audiovault.repository.PhraseRepository;
import jp.speakbuddy.audiovault.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class RequestEvaluatorTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PhraseRepository phraseRepository;

    @InjectMocks
    private RequestEvaluator requestEvaluator;

    private final Long validUserId = 1L;
    private final Long validPhraseId = 10L;

    @SuppressWarnings("unused")
    private Exception assertThrows;

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        User mockUser = mock(User.class);
        when(userRepository.findById(validUserId)).thenReturn(Optional.of(mockUser));

        assertDoesNotThrow(() -> requestEvaluator.validateUser(userRepository, validUserId));
    }

    @Test
    void validateUser_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(validUserId)).thenReturn(Optional.empty());

        assertThrows = assertThrowsExactly(ResourceNotFoundException.class, 
            () -> requestEvaluator.validateUser(userRepository, validUserId),
            "User ID " + validUserId + " not found"
        );
    }

    @Test
    void validatePhrase_ShouldSetPhrase_WhenPhraseExists() {
        Phrase mockPhrase = mock(Phrase.class);
        when(phraseRepository.findById(validPhraseId)).thenReturn(Optional.of(mockPhrase));

        assertDoesNotThrow(() -> requestEvaluator.validatePhrase(phraseRepository, validPhraseId));
    }

    @Test
    void validatePhrase_ShouldThrowException_WhenPhraseNotFound() {
        when(phraseRepository.findById(validPhraseId)).thenReturn(Optional.empty());

        assertThrows = assertThrowsExactly(ResourceNotFoundException.class, 
            () -> requestEvaluator.validatePhrase(phraseRepository, validPhraseId),
            "Phrase Id " + validPhraseId + " not found"
        );
    }

    @Test
    void validatePhraseOwnership_ShouldPass_WhenUserOwnsPhrase() {
        User mockUser = mock(User.class);
        Phrase mockPhrase = mock(Phrase.class);

        when(userRepository.findById(validUserId)).thenReturn(Optional.of(mockUser));
        when(phraseRepository.findById(validPhraseId)).thenReturn(Optional.of(mockPhrase));

        assertDoesNotThrow(() -> requestEvaluator
            .validateUser(userRepository, validUserId)
            .validatePhrase(phraseRepository, validPhraseId)
            .validatePhraseOwnership());
    }

    @Test
    void validatePhraseOwnership_ShouldThrowException_WhenUserNotSet() {
        requestEvaluator = new RequestEvaluator(); // No user set

        assertThrows = assertThrowsExactly(ValidationException.class, 
            () -> requestEvaluator.validatePhraseOwnership(),
            "User or Phrase is not found"
        );
    }

    @Test
    void validatePhraseOwnership_ShouldThrowException_WhenPhraseNotSet() {
        User mockUser = mock(User.class);

        when(userRepository.findById(validUserId)).thenReturn(Optional.of(mockUser));
        requestEvaluator.validateUser(userRepository, validUserId); // User set but no phrase

        assertThrows = assertThrowsExactly(ValidationException.class, 
            () -> requestEvaluator.validatePhraseOwnership(),
            "User or Phrase is not found"
        );
    }

    @Test
    void validatePhraseOwnership_ShouldThrowException_WhenUserDoesNotOwnPhrase() {
        User mockUser = mock(User.class);
        Phrase mockPhrase = mock(Phrase.class);

        when(userRepository.findById(validUserId)).thenReturn(Optional.of(mockUser));
        when(phraseRepository.findById(validPhraseId)).thenReturn(Optional.of(mockPhrase));

        // Mock a different owner ID
        when(mockPhrase.getUserId()).thenReturn(999L);

        assertThrows = assertThrowsExactly(ValidationException.class, 
            () -> requestEvaluator
                .validateUser(userRepository, validUserId)
                .validatePhrase(phraseRepository, validPhraseId)
                .validatePhraseOwnership(),
            "Phrase " + validPhraseId + " is not owned by user " + validUserId
        );
    }
}