package jp.speakbuddy.audiovault.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jp.speakbuddy.audiovault.model.Phrase;
import jp.speakbuddy.audiovault.model.User;
import jp.speakbuddy.audiovault.repository.PhraseRepository;
import jp.speakbuddy.audiovault.repository.UserRepository;

@RestController
@RequestMapping("/datatest")
public class DataTestController {
    @Autowired private UserRepository userRepository;
    @Autowired private PhraseRepository phraseRepository;
    
    @PostMapping
    public ResponseEntity<String> addDataTest(
        @RequestParam Long userId,
        @RequestParam Long phraseId
    ) {
        if (userId > 0) {
            userRepository.save(new User(userId));
        }
        if (userId > 0 && phraseId > 0) {
            phraseRepository.save(new Phrase(phraseId, userId));
        }
        return ResponseEntity.ok("Successfully add test data");
    }

}
