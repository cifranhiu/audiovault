package jp.speakbuddy.audiovault.service;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("deprecation")
public class AudioConverter {

    private static final String FFMPEG_PATH = "ffmpeg"; // Make sure ffmpeg is installed and available in PATH
    
    public static File convertAudioToWav(File inputFile, String outputPath) throws Exception {

        String outputFileName = inputFile.getAbsolutePath().replace(".m4a", ".wav");
        File outputFile = new File(outputFileName);

        // Set up the ProcessBuilder to run the FFmpeg command
        ProcessBuilder builder = new ProcessBuilder(
                FFMPEG_PATH, "-i", inputFile.getAbsolutePath(), outputFileName
        );

        builder.redirectErrorStream(true);  // Merge stdout and stderr
        Process process = builder.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new IOException("FFmpeg conversion failed with exit code " + exitCode);
        }

        return outputFile; // Return the output WAV file
    }

    public static void convertWavToFormat(String inputPath, String outputPath, String format) throws Exception {
        String command = String.format("ffmpeg -i %s %s", inputPath, outputPath);
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();
    }
}