package jp.speakbuddy.audiovault.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.web.multipart.MultipartFile;

import jp.speakbuddy.audiovault.exception.ValidationException;

public class AudioFileEvaluator {
    private static final String AUDIO_STORAGE_DIR = "audio_files/";
    private static final String FFMPEG_PATH = "ffmpeg";

    private final MultipartFile inputFile;
    private Path tempFile;
    private String convertedFile;

    public AudioFileEvaluator(MultipartFile inputFile) {
        this.inputFile = inputFile;
    }

    public AudioFileEvaluator storeTemp(String format) throws Exception {
        this.tempFile = Files.createTempFile("audio_upload_tmp_", "." + format);
        Files.copy(inputFile.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
        return this;
    }

    public AudioFileEvaluator validateFileFormat(String acceptedFormat) throws Exception {
        // Check file extension first (quick check)
        String originalFileName = inputFile.getOriginalFilename();
        if (originalFileName == null || !originalFileName.toLowerCase().endsWith("." + acceptedFormat)) {
            Files.delete(tempFile);
            throw new ValidationException("Invalid file format. Only ." + acceptedFormat + " files are allowed.");
        }
        // Validate MIME type
        String mimeType = Files.probeContentType(tempFile.getFileName());
        if (mimeType == null || !mimeType.equals("audio/mp4")) {
            Files.delete(tempFile);
            throw new ValidationException("Invalid file type. Only M4A (audio/mp4) files are allowed.");
        }
        return this;
    }

    public AudioFileEvaluator convertFile(String fromFormat, String toFormat) throws Exception {
        // // Generate a unique filename (output)
        // String fileName = userId + "_" + phraseId + "_" + System.currentTimeMillis();
        // String filePath =  + fileName + "." + toFormat;
        // File outputFile = new File(outputFileName);

        Path path = Paths.get(AUDIO_STORAGE_DIR);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        //AUDIO_STORAGE_DIR + "/" + toFormat + "/" + 
        String outputFileName = tempFile.toFile().getAbsolutePath().replace(".m4a", ".wav");

        int exitCode = new ProcessBuilder(FFMPEG_PATH, "-i", tempFile.toFile().getAbsolutePath(), outputFileName)
            .redirectErrorStream(true)
            .start()
            .waitFor();

        if (exitCode != 0) {
            throw new IOException("FFmpeg conversion failed with exit code " + exitCode);
        }
        this.convertedFile = outputFileName;
        return this;
    }

    public AudioFileEvaluator deleteTemp() throws Exception {
        Files.delete(tempFile);
        return this;
    }

    public File store() throws Exception {
        return new File(convertedFile);
    }
    
}
