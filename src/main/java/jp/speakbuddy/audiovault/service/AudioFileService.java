package jp.speakbuddy.audiovault.service;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jp.speakbuddy.audiovault.exception.ValidationException;
import jp.speakbuddy.audiovault.model.AudioFile;
import jp.speakbuddy.audiovault.repository.AudioFileRepository;
import jp.speakbuddy.audiovault.repository.PhraseRepository;
import jp.speakbuddy.audiovault.repository.UserRepository;

@Service
public class AudioFileService {
    @Autowired private UserRepository userRepository;
    @Autowired private PhraseRepository phraseRepository;
    @Autowired private AudioFileRepository audioFileRepository;
    private static final String STORED_FORMAT = "wav";
    private static final String ACCEPTED_FORMAT = "m4a";

    public AudioFile storeAudioFile(MultipartFile inputFile, Long userId, Long phraseId) throws Exception {
        new RequestEvaluator()
            .validateUser(userRepository, userId)
            .validatePhrase(phraseRepository, phraseId)
            .validatePhraseOwnership();

        var outputFile = new AudioFileEvaluator(inputFile)
            .storeTemp(ACCEPTED_FORMAT)
            .validateFileFormat(ACCEPTED_FORMAT)
            .convertFile(ACCEPTED_FORMAT, STORED_FORMAT)
            .deleteTemp()
            .store();

        AudioFile audioFileEntity = new AudioFile();
        Optional<AudioFile> audioFile = audioFileRepository.findByUserIdAndPhraseId(userId, phraseId);
        if (audioFile.isPresent()) {
            audioFileEntity = audioFile.get();
        }
        audioFileEntity.setUserId(userId);
        audioFileEntity.setPhraseId(phraseId);
        audioFileEntity.setWavFilepath(outputFile.getAbsolutePath());
        audioFileRepository.save(audioFileEntity);
        return audioFileEntity;
    }

    @SuppressWarnings("ConvertToStringSwitch")
    public File retrieveAudioFile(Long userId, Long phraseId, String format) throws IOException {
        new RequestEvaluator()
            .validateUser(userRepository, userId)
            .validatePhrase(phraseRepository, phraseId)
            .validatePhraseOwnership();
        
        Optional<AudioFile> audioFile = audioFileRepository.findByUserIdAndPhraseId(userId, phraseId);
        if (audioFile.isEmpty()) {
            throw new ValidationException("Audio file not found");
        }
        var audioGet = audioFile.get();

        // Get File
        if (format.equals("m4a")) {
            if (audioGet.getM4aFilepath() != null) {
                return new File(audioGet.getM4aFilepath());
            }
            if (audioGet.getWavFilepath() != null) {
                // var wav = audioGet.getWavFilepath();
                // var m4a = new AudioFileEvaluator(wav).convertFile(STORED_FORMAT).store();
                // audioGet.setM4aFilepath(m4a);
                // audioFileRepository.save(audioGet);
                // return m4a;
            }
        } else if (format.equals("wav")) { 
            if (audioGet.getWavFilepath() != null) {
                return new File(audioGet.getWavFilepath());
            }
        }
        throw new ValidationException("Audio file is not available in "+format+" format"); 

    }
}