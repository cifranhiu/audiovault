package jp.speakbuddy.audiovault.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "`phrases`")
@Data
public class Phrase {
    @Id
    private Long id;
    
    private Long userId;
}
