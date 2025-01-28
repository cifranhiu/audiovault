package jp.speakbuddy.audiovault.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import jp.speakbuddy.audiovault.exception.ValidationException;

public class AudioFileEvaluator {
    private static final String AUDIO_STORAGE_DIR = "audio_files/";
    private static final String FFMPEG_PATH = "ffmpeg";

    private final InputStream inputFileStream;
    private final String inputFileName;
    protected Path tempFile;
    private String convertedFile;

    public AudioFileEvaluator(InputStream inputFileStream, String inputFileName) {
        this.inputFileStream = inputFileStream;
        this.inputFileName = inputFileName;
    }

    public AudioFileEvaluator storeTemp(String format) throws Exception {
        this.tempFile = Files.createTempFile("audio_upload_", "." + format);
        Files.copy(inputFileStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
        return this;
    }

    public AudioFileEvaluator validateFileFormat(String acceptedFormat) throws Exception {
        // Check file extension first (quick check)
        String originalFileName = inputFileName;
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
        return convertFile(new ProcessBuilder(), fromFormat, toFormat);
    }
    
    public AudioFileEvaluator convertFile(ProcessBuilder pb, String fromFormat, String toFormat) throws Exception {
        Path path = Paths.get(AUDIO_STORAGE_DIR + "/" + toFormat + "/");
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        String outputFileName = AUDIO_STORAGE_DIR + "/" + toFormat + "/" + tempFile.toFile().getName().replace("."+fromFormat, "."+toFormat);
        int exitCode = pb.command(FFMPEG_PATH, "-i", tempFile.toFile().getAbsolutePath(), outputFileName)
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

    public File file() throws Exception {
        return new File(convertedFile);
    }
    
}
