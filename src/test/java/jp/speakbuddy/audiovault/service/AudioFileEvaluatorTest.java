package jp.speakbuddy.audiovault.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import jp.speakbuddy.audiovault.exception.ValidationException;


@ExtendWith(MockitoExtension.class)
class AudioFileEvaluatorTest {

    @Mock
    private InputStream inputFileStream;
    
    @InjectMocks
    private AudioFileEvaluator audioFileEvaluator;

    @Mock
    private ProcessBuilder processBuilder;

    @Mock
    private Process process;

    @SuppressWarnings("unused")
    private Exception assertThrows;

    @Test
    void testValidateFileFormat_InvalidFormat() {
        audioFileEvaluator = new AudioFileEvaluator(inputFileStream, "test.mp3");
        assertThrows = assertThrows(ValidationException.class, () -> {
            audioFileEvaluator.storeTemp("mp4").validateFileFormat("m4a");
        });
    }

    @Test
    void testValidateFileFormat_InvalidMimeType() throws Exception {
        audioFileEvaluator = new AudioFileEvaluator(inputFileStream, "test.m4a");
        assertThrows = assertThrows(ValidationException.class, () -> {
            audioFileEvaluator.storeTemp("m4a").validateFileFormat("m4a");
        });
    }

    @Test
    void testFileReturnsConvertedFile() throws Exception {
        //mocks
        when(processBuilder.command(anyString(), anyString(), anyString(), anyString())).thenReturn(processBuilder);
        when(processBuilder.redirectErrorStream(true)).thenReturn(processBuilder);
        when(processBuilder.start()).thenReturn(process);
        when(process.waitFor()).thenReturn(0); // Simulate success

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.exists(any())).thenReturn(false);
        }

        audioFileEvaluator = new AudioFileEvaluator(inputFileStream, "test.m4a");
        audioFileEvaluator.storeTemp("m4a");
        audioFileEvaluator.convertFile(processBuilder, "m4a", "mp3");

        // Verify the method calls and assert results
        verify(processBuilder).start();
        assertNotNull(audioFileEvaluator.file());

        // Verify deleteTemp method
        audioFileEvaluator.deleteTemp();
        assertFalse(Files.exists(audioFileEvaluator.tempFile));

        assertThrows = assertThrowsExactly(IOException.class, () -> {
            audioFileEvaluator.convertFile("m4a", "wav");
        });
    }
}
