package MindChatBot.mindChatBot.repository;

import MindChatBot.mindChatBot.model.JournalEntry;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface JournalEntryRepository extends MongoRepository<JournalEntry, String> {
    List<JournalEntry> findByDate(LocalDate date); // Find entries by LocalDate
}