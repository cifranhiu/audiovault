package jp.speakbuddy.audiovault.controller;

import java.io.File;
import java.nio.file.Files;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import jp.speakbuddy.audiovault.model.AudioFile;
import jp.speakbuddy.audiovault.service.AudioFileService;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unused")
public class AudioFileControllerTest {

    @Mock
    private AudioFileService audioFileService;

    @InjectMocks
    private AudioFileController audioFileController;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(audioFileController).build();
    }

    @Test
    void testUploadAudio() throws Exception {
        // Arrange
        Long userId = 1L;
        Long phraseId = 2L;
        MockMultipartFile mockFile = new MockMultipartFile("audio_file", "test-audio.mp3", "audio/mpeg", "audio data".getBytes());
        AudioFile mockAudioFileDTO = new AudioFile(); // Assume a constructor or setter for mock data
        when(audioFileService.storeAudioFile(any(), eq(userId), eq(phraseId))).thenReturn(mockAudioFileDTO);

        // Act & Assert
        mockMvc.perform(multipart("/audio/user/{userId}/phrase/{phraseId}", userId, phraseId)
                        .file(mockFile))
                .andExpect(status().isOk())
                .andExpect(content().json("{ /* expected JSON response here */ }"));
    }

    @Test
    void testGetAudio() throws Exception {
        // Arrange
        Long userId = 1L;
        Long phraseId = 2L;
        String audioFormat = "mp3";
        File file = new File("path/to/test-audio.mp3");
        when(audioFileService.retrieveAudioFile(eq(userId), eq(phraseId), eq(audioFormat))).thenReturn(file);
        
        file.getParentFile().mkdirs();
        Files.write(file.toPath(), new byte[]{});

        // Act & Assert
        mockMvc.perform(get("/audio/user/{userId}/phrase/{phraseId}/{audioFormat}", userId, phraseId, audioFormat))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=" + file.getName()))
                .andExpect(header().string("Content-Type", "audio/" + audioFormat))
                .andExpect(content().bytes( new byte[]{}));

        Files.delete(file.toPath());
    }
}