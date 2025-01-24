package jp.speakbuddy.audiovault.dto;

import jp.speakbuddy.audiovault.model.AudioFile;
import lombok.Data;

@Data
public class AudioFileDTO {
    String wavFile;
    String m4aFile;

    public static AudioFileDTO from(AudioFile stored) {
        var result = new AudioFileDTO();
        result.setWavFile(stored.getFileWAV());
        result.setM4aFile(stored.getFileM4A());
        return result;
    }
    
}
