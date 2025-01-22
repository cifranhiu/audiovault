package jp.speakbuddy.audiovault.service;

@SuppressWarnings("deprecation")
public class AudioConverter {

    public static void convertAudioToWav(String inputPath, String outputPath) throws Exception {
        String command = String.format("ffmpeg -i %s %s", inputPath, outputPath);
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();
    }

    public static void convertWavToFormat(String inputPath, String outputPath, String format) throws Exception {
        String command = String.format("ffmpeg -i %s %s", inputPath, outputPath);
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();
    }
}