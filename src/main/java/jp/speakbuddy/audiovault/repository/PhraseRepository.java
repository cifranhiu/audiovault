package jp.speakbuddy.audiovault.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.speakbuddy.audiovault.model.Phrase;

@Repository
public interface PhraseRepository extends JpaRepository<Phrase, Long> {
}