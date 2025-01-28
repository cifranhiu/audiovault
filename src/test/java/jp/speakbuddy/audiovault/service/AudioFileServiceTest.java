package jp.speakbuddy.audiovault.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import jp.speakbuddy.audiovault.exception.ResourceNotFoundException;
import jp.speakbuddy.audiovault.exception.ValidationException;
import jp.speakbuddy.audiovault.model.AudioFile;
import jp.speakbuddy.audiovault.model.Phrase;
import jp.speakbuddy.audiovault.model.User;
import jp.speakbuddy.audiovault.repository.AudioFileRepository;
import jp.speakbuddy.audiovault.repository.PhraseRepository;
import jp.speakbuddy.audiovault.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unused") 
class AudioFileServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PhraseRepository phraseRepository;
    @Mock private AudioFileRepository audioFileRepository;
    @Mock private MultipartFile multipartFile;

    @InjectMocks private AudioFileService audioFileService; // Assuming `storeAudioFile` is in `audioFileService`

    private static final String ACCEPTED_FORMAT = "m4a";
    private static final String STORED_FORMAT = "wav";

    private Exception assertThrows;

    @Test
    void storeAudioFile_ShouldStore_WhenValidRequest() throws Exception {
        // Given
        Long userId = 1L;
        Long phraseId = 2L;
        String fileName = "test.m4a";
        File mockOutputFile = new File("mock/path/output.wav");

        // Mock MultipartFile
        InputStream mockInputStream = new ByteArrayInputStream("fake audio data".getBytes());
        when(multipartFile.getInputStream()).thenReturn(mockInputStream);
        when(multipartFile.getOriginalFilename()).thenReturn(fileName);

        // Mock User and Phrase Validation
        User mockUser = new User();
        mockUser.setId(userId);
        Phrase mockPhrase = new Phrase();
        mockPhrase.setId(phraseId);
        mockPhrase.setUserId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(phraseRepository.findById(phraseId)).thenReturn(Optional.of(mockPhrase));

        // Mock Audio Processing
        try (var mockedAudioEvaluator =
                     mockConstruction(AudioFileEvaluator.class, (mock, context) -> {
                         when(mock.storeTemp(ACCEPTED_FORMAT)).thenReturn(mock);
                         when(mock.validateFileFormat(ACCEPTED_FORMAT)).thenReturn(mock);
                         when(mock.convertFile(ACCEPTED_FORMAT, STORED_FORMAT)).thenReturn(mock);
                         when(mock.deleteTemp()).thenReturn(mock);
                         when(mock.file()).thenReturn(mockOutputFile);
                     })) {

            // Mock existing AudioFile check
            AudioFile existingAudioFile = new AudioFile();
            when(audioFileRepository.findByUserIdAndPhraseId(userId, phraseId)).thenReturn(Optional.of(existingAudioFile));

            // When
            AudioFile result = audioFileService.storeAudioFile(multipartFile, userId, phraseId);

            // Then
            assertNotNull(result);
            assertEquals(userId, result.getUserId());
            assertEquals(phraseId, result.getPhraseId());
            assertTrue(result.getWavFilepath().endsWith("/mock/path/output.wav"));

            // Verify repository interactions
            verify(audioFileRepository).save(existingAudioFile);
        }
    }

    @Test
    void storeAudioFile_ShouldThrowException_WhenUserNotFound() {
        // Given
        Long userId = 1L;
        Long phraseId = 2L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows = assertThrows(ResourceNotFoundException.class, () ->
            audioFileService.storeAudioFile(multipartFile, userId, phraseId)
        );
    }

    @Test
    void storeAudioFile_ShouldThrowException_WhenPhraseNotFound() {
        // Given
        Long userId = 1L;
        Long phraseId = 2L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(phraseRepository.findById(phraseId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows = assertThrows(ResourceNotFoundException.class, () ->
            audioFileService.storeAudioFile(multipartFile, userId, phraseId)
        );
    }

    @Test
    void storeAudioFile_ShouldThrowException_WhenPhraseOwnershipInvalid() {
        // Given
        Long userId = 1L;
        Long phraseId = 2L;
        User user = new User();
        user.setId(userId);
        Phrase phrase = new Phrase();
        phrase.setId(phraseId);
        phrase.setUserId(99L); // Different user ID

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(phraseRepository.findById(phraseId)).thenReturn(Optional.of(phrase));

        // When & Then
        assertThrows = assertThrows(ValidationException.class, () ->
            audioFileService.storeAudioFile(multipartFile, userId, phraseId)
        );
    }

     @Test
    void retrieveAudioFile_ShouldReturnM4A_WhenM4AFileExists() throws Exception {
        // Given
        Long userId = 1L;
        Long phraseId = 2L;
        String format = "m4a";
        String existingM4aPath = "mock/path/audio.m4a";

        AudioFile audioFile = new AudioFile();
        audioFile.setM4aFilepath(existingM4aPath);

        // Mock User and Phrase Validation
        User mockUser = new User();
        mockUser.setId(userId);
        Phrase mockPhrase = new Phrase();
        mockPhrase.setId(phraseId);
        mockPhrase.setUserId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(phraseRepository.findById(phraseId)).thenReturn(Optional.of(mockPhrase));
        when(audioFileRepository.findByUserIdAndPhraseId(userId, phraseId)).thenReturn(Optional.of(audioFile));

        // When
        File result = audioFileService.retrieveAudioFile(userId, phraseId, format);

        // Then
        assertNotNull(result);
        assertTrue(result.getAbsolutePath().endsWith(existingM4aPath));
    }

    @Test
    void retrieveAudioFile_ShouldConvertWAVToM4A_WhenM4ANotExists() throws Exception {
        // Given
        Long userId = 1L;
        Long phraseId = 2L;
        String format = "m4a";
        String existingWavPath = "mock/path/audio.wav";
        String convertedM4aPath = "mock/path/converted_audio.m4a";

        AudioFile audioFile = new AudioFile();
        audioFile.setWavFilepath(existingWavPath);
        
        File mockWavFile = new File(existingWavPath);
        mockWavFile.getParentFile().mkdirs();
        Files.write(mockWavFile.toPath(), new byte[]{});

        File mockM4aFile = new File(convertedM4aPath);

        // Mock User and Phrase Validation
        User mockUser = new User();
        mockUser.setId(userId);
        Phrase mockPhrase = new Phrase();
        mockPhrase.setId(phraseId);
        mockPhrase.setUserId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(phraseRepository.findById(phraseId)).thenReturn(Optional.of(mockPhrase));
        when(audioFileRepository.findByUserIdAndPhraseId(userId, phraseId)).thenReturn(Optional.of(audioFile));

        // Mock conversion process
        try (MockedConstruction<AudioFileEvaluator> mockedAudioEvaluator =
                    mockConstruction(AudioFileEvaluator.class, (mock, context) -> {
                        when(mock.storeTemp("wav")).thenReturn(mock);
                        when(mock.convertFile("wav", "m4a")).thenReturn(mock);
                        when(mock.deleteTemp()).thenReturn(mock);
                        when(mock.file()).thenReturn(mockM4aFile);
                    })) {

            // When
            File result = audioFileService.retrieveAudioFile(userId, phraseId, format);

            // Then
            assertNotNull(result);
            assertTrue(result.getAbsolutePath().endsWith(convertedM4aPath));
            assertTrue(audioFile.getM4aFilepath().endsWith(convertedM4aPath));
            verify(audioFileRepository).save(audioFile);
        }
        Files.delete(mockWavFile.toPath());
    }

    @Test
    void retrieveAudioFile_ShouldReturnWAV_WhenWAVFileExists() throws Exception {
        // Given
        Long userId = 1L;
        Long phraseId = 2L;
        String format = "wav";
        String existingWavPath = "mock/path/audio.wav";

        AudioFile audioFile = new AudioFile();
        audioFile.setWavFilepath(existingWavPath);

        // Mock User and Phrase Validation
        User mockUser = new User();
        mockUser.setId(userId);
        Phrase mockPhrase = new Phrase();
        mockPhrase.setId(phraseId);
        mockPhrase.setUserId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(phraseRepository.findById(phraseId)).thenReturn(Optional.of(mockPhrase));
        when(audioFileRepository.findByUserIdAndPhraseId(userId, phraseId)).thenReturn(Optional.of(audioFile));

        // When
        File result = audioFileService.retrieveAudioFile(userId, phraseId, format);

        // Then
        assertNotNull(result);
        assertTrue(result.getAbsolutePath().endsWith(existingWavPath));
    }

    @Test
    void retrieveAudioFile_ShouldThrowException_WhenAudioFileNotFound() {
        // Given
        Long userId = 1L;
        Long phraseId = 2L;

        // When & Then
        assertThrows = assertThrows(ResourceNotFoundException.class, () ->
            audioFileService.retrieveAudioFile(userId, phraseId, "wav")
        );
    }

    @Test
    void retrieveAudioFile_ShouldThrowException_WhenFormatNotAvailable() {
        // Given
        Long userId = 1L;
        Long phraseId = 2L;
        String format = "m4a";

        AudioFile audioFile = new AudioFile(); // No file paths set (both `m4a` and `wav` are null)

        // When & Then
        var exception = assertThrows(ResourceNotFoundException.class, () ->
            audioFileService.retrieveAudioFile(userId, phraseId, format)
        );
        assertEquals("User ID 1 not found", exception.getMessage());
    }
}