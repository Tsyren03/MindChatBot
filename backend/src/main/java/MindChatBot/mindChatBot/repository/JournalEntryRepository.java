package MindChatBot.mindChatBot.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import MindChatBot.mindChatBot.model.JournalEntry;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface JournalEntryRepository extends MongoRepository<JournalEntry, String> {
    List<JournalEntry> findByUserId(String userId);
    Optional<JournalEntry> findByUserIdAndDate(String userId, LocalDate date);

    // ✅ 추가: 날짜에 해당하는 모든 노트 (목록 반환용)
    List<JournalEntry> findAllByUserIdAndDate(String userId, LocalDate date);
}
