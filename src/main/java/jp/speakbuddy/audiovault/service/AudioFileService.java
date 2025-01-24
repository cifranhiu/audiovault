package jp.speakbuddy.audiovault.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jp.speakbuddy.audiovault.exception.ResourceNotFoundException;
import jp.speakbuddy.audiovault.exception.ValidationException;
import jp.speakbuddy.audiovault.model.AudioFile;
import jp.speakbuddy.audiovault.model.Phrase;
import jp.speakbuddy.audiovault.model.User;
import jp.speakbuddy.audiovault.repository.AudioFileRepository;
import jp.speakbuddy.audiovault.repository.PhraseRepository;
import jp.speakbuddy.audiovault.repository.UserRepository;

@Service
public class AudioFileService {
    @Autowired private UserRepository userRepository;
    @Autowired private PhraseRepository phraseRepository;
    @Autowired private AudioFileRepository audioFileRepository;

    private static final String AUDIO_STORAGE_DIR = "audio_files/";
    private static final String STORED_FORMAT = "wav";
    private static final String ACCEPTED_FORMAT = "m4a";

    public AudioFile storeAudioFile(MultipartFile inputFile, Long userId, Long phraseId) throws Exception {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User ID " + userId + " not found"));

        Phrase phrase = phraseRepository.findById(phraseId)
            .orElseThrow(() -> new ResourceNotFoundException("Phrase Id " + phraseId + " not found"));

        if (!Objects.equals(phrase.getUserId(), user.getId())) {
            throw new ValidationException("Phrase " + phraseId + " is not owned by user "+userId);
        }

        // Check file extension first (quick check)
        String originalFileName = inputFile.getOriginalFilename();
        if (originalFileName == null || !originalFileName.toLowerCase().endsWith("." + ACCEPTED_FORMAT)) {
            throw new ValidationException("Invalid file format. Only ." + ACCEPTED_FORMAT + " files are allowed.");
        }

        // Save to a temporary file
        Path tempFile = Files.createTempFile("audio_upload", "." + ACCEPTED_FORMAT);
        Files.copy(inputFile.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

        // Validate MIME type
        String mimeType = Files.probeContentType(tempFile);
        if (mimeType == null || !mimeType.equals("audio/mp4")) {
            throw new ValidationException("Invalid file type. Only M4A (audio/mp4) files are allowed.");
        }

        // Create directories if they do not exist
        Path path = Paths.get(AUDIO_STORAGE_DIR);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        // Generate a unique filename
        String fileName = userId + "_" + phraseId + "_" + System.currentTimeMillis() + "." + STORED_FORMAT;
        String filePath = AUDIO_STORAGE_DIR + fileName;

        // Convert to WAV
        AudioConverter.convertAudioToWav(tempFile.toFile(), filePath);

        // Store metadata
        AudioFile audioFileEntity = new AudioFile();
        audioFileEntity.setUserId(userId);
        audioFileEntity.setPhraseId(phraseId);
        audioFileEntity.setFileWAV(filePath);
        audioFileEntity.setFileM4A(tempFile.toString());
        audioFileRepository.save(audioFileEntity);

        return audioFileEntity;
    }

    public File retrieveAudioFile(Long userId, Long phraseId, String format) throws IOException {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User ID " + userId + " not found"));

        Phrase phrase = phraseRepository.findById(phraseId)
            .orElseThrow(() -> new ResourceNotFoundException("Phrase Id " + phraseId + " not found"));

        if (!Objects.equals(phrase.getUserId(), user.getId())) {
            throw new ValidationException("Phrase " + phraseId + " is not owned by user "+userId);
        }
        
        // Optional<AudioFile> audioFile = audioFileRepository.findByUserIdAndPhraseId(userId, phraseId);
        // if (audioFile.isEmpty()) {
        //     throw new IOException("Audio file not found");
        // }

        // String originalFilePath = audioFile.get().getFilePath();
        // String outputFilePath = AUDIO_STORAGE_DIR + userId + "_" + phraseId + "." + format;

        // // Convert the file to the desired format
        // AudioConverter.convertWavToFormat(originalFilePath, outputFilePath, format);

        // return new File(outputFilePath);
        return null;
    }
}