package jp.speakbuddy.audiovault.controller;

import java.io.File;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jp.speakbuddy.audiovault.dto.AudioFileDTO;
import jp.speakbuddy.audiovault.service.AudioFileService;

@RestController
@RequestMapping("/audio")
public class AudioFileController {

    @Autowired
    private AudioFileService audioFileService;

    @PostMapping("/user/{userId}/phrase/{phraseId}")
    public ResponseEntity<AudioFileDTO> uploadAudio(
        @RequestParam("audio_file") MultipartFile inputFile,
        @PathVariable Long userId,
        @PathVariable Long phraseId
    ) throws Exception {
        var stored = audioFileService.storeAudioFile(inputFile, userId, phraseId);
        return ResponseEntity.ok(AudioFileDTO.from(stored));
    }

    @GetMapping("/user/{userId}/phrase/{phraseId}/{audioFormat}")
    public ResponseEntity<byte[]> getAudio(
        @PathVariable Long userId,
        @PathVariable Long phraseId,
        @PathVariable String audioFormat
    ) throws Exception {
        File file = audioFileService.retrieveAudioFile(userId, phraseId, audioFormat);
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        return ResponseEntity.ok()
                .header(CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
                .header(CONTENT_TYPE, "audio/"+audioFormat)
                .body(fileBytes);
    }
}