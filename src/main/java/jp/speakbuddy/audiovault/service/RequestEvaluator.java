package jp.speakbuddy.audiovault.service;

import java.util.Objects;

import jp.speakbuddy.audiovault.exception.ResourceNotFoundException;
import jp.speakbuddy.audiovault.exception.ValidationException;
import jp.speakbuddy.audiovault.model.Phrase;
import jp.speakbuddy.audiovault.model.User;
import jp.speakbuddy.audiovault.repository.PhraseRepository;
import jp.speakbuddy.audiovault.repository.UserRepository;

public class RequestEvaluator {
    private User user = null;
    private Phrase phrase = null;

    public RequestEvaluator validateUser(UserRepository userRepository, Long userId) {
        this.user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User ID " + userId + " not found"));
        return this;
    }

    public RequestEvaluator validatePhrase(PhraseRepository phraseRepository, Long phraseId) {
        this.phrase = phraseRepository.findById(phraseId)
            .orElseThrow(() -> new ResourceNotFoundException("Phrase Id " + phraseId + " not found"));
        return this;
    }

    public RequestEvaluator validatePhraseOwnership() {
        if (user == null || phrase == null) {
            throw new ValidationException("User or Phrase is not found");
        }
        if (!Objects.equals(user.getId(), this.phrase.getUserId())) {
            throw new ValidationException("Phrase "+phrase.getId()+" is not owned by user "+user.getId());
        }
        return this;
    }
    
}
