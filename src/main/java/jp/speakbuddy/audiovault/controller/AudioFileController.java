package jp.speakbuddy.audiovault.controller;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jp.speakbuddy.audiovault.service.AudioFileService;

@RestController
@RequestMapping("/audio")
public class AudioFileController {

    @Autowired
    private AudioFileService audioFileService;

    @PostMapping("/user/{userId}/phrase/{phraseId}")
    public ResponseEntity<String> uploadAudio(
        @RequestParam("audio_file") MultipartFile audioFile,
        @PathVariable Long userId,
        @PathVariable Long phraseId
    ) throws Exception {
        String filePath = audioFileService.storeAudioFile(audioFile, userId, phraseId);
        return ResponseEntity.ok("File uploaded and stored at: " + filePath);
    }

    @GetMapping("/user/{userId}/phrase/{phraseId}/{audioFormat}")
    public ResponseEntity<File> getAudio(
        @PathVariable Long userId,
        @PathVariable Long phraseId,
        @PathVariable String audioFormat
    ) throws Exception {
        File file = audioFileService.retrieveAudioFile(userId, phraseId, audioFormat);
        return ResponseEntity.ok()
                .header(CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
                .body(file);
    }
}