package jp.speakbuddy.audiovault.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.speakbuddy.audiovault.model.AudioFile;

@Repository
public interface AudioFileRepository extends JpaRepository<AudioFile, Long> {
    Optional<AudioFile> findByUserIdAndPhraseId(Long userId, Long phraseId);
}