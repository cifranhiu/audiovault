package jp.speakbuddy.audiovault.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.speakbuddy.audiovault.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}