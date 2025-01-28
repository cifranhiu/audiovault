package jp.speakbuddy.audiovault.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import jp.speakbuddy.audiovault.model.Phrase;
import jp.speakbuddy.audiovault.model.User;
import jp.speakbuddy.audiovault.repository.PhraseRepository;
import jp.speakbuddy.audiovault.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unused")
public class DataTestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PhraseRepository phraseRepository;

    @InjectMocks
    private DataTestController dataTestController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(dataTestController).build();
    }

    @Test
    void testAddDataTest_Success() throws Exception {
        // Arrange
        Long userId = 1L;
        Long phraseId = 2L;

        // Act & Assert
        mockMvc.perform(post("/datatest")
                .param("userId", userId.toString())
                .param("phraseId", phraseId.toString())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully add test data"));

        // Verify interactions
        verify(userRepository, times(1)).save(any(User.class));
        verify(phraseRepository, times(1)).save(any(Phrase.class));
    }

    @Test
    void testAddDataTest_OnlyUserSaved() throws Exception {
        // Arrange
        Long userId = 1L;
        Long phraseId = 0L; // phraseId is invalid

        // Act & Assert
        mockMvc.perform(post("/datatest")
                .param("userId", userId.toString())
                .param("phraseId", phraseId.toString())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully add test data"));

        // Verify that only userRepository.save() is called
        verify(userRepository, times(1)).save(any(User.class));
        verify(phraseRepository, never()).save(any(Phrase.class));
    }

    @Test
    void testAddDataTest_NoDataSaved() throws Exception {
        // Arrange
        Long userId = 0L;
        Long phraseId = 0L;

        // Act & Assert
        mockMvc.perform(post("/datatest")
                .param("userId", userId.toString())
                .param("phraseId", phraseId.toString())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully add test data"));

        // Verify that neither repository is called
        verify(userRepository, never()).save(any(User.class));
        verify(phraseRepository, never()).save(any(Phrase.class));
    }
}
