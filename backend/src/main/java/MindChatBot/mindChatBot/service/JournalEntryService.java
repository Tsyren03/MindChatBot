package MindChatBot.mindChatBot.service;

import MindChatBot.mindChatBot.model.JournalEntry;
import MindChatBot.mindChatBot.repository.JournalEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class JournalEntryService {

    private final JournalEntryRepository journalEntryRepository;

    @Autowired
    public JournalEntryService(JournalEntryRepository journalEntryRepository) {
        this.journalEntryRepository = journalEntryRepository;
    }

    // Create a new journal entry
    public JournalEntry createJournalEntry(String userId, String content, LocalDate date) {
        JournalEntry journalEntry = new JournalEntry();
        journalEntry.setUserId(userId);
        journalEntry.setContent(content);
        journalEntry.setDate(date);
        journalEntry.setTimestamp(LocalDateTime.now());

        return journalEntryRepository.save(journalEntry);
    }

    // Get all journal entries for a specific user
    public List<JournalEntry> getAllEntriesForUser(String userId) {
        return journalEntryRepository.findByUserId(userId);
    }

    // Get a specific journal entry by userId and date (optional - for future use)
    public Optional<JournalEntry> getEntryByUserAndDate(String userId, LocalDate date) {
        return journalEntryRepository.findByUserIdAndDate(userId, date);
    }

    // Get all journal entries for a specific user on a specific date
    public List<JournalEntry> getEntriesForUserByDate(String userId, LocalDate date) {
        return journalEntryRepository.findAllByUserIdAndDate(userId, date);
    }

    // ✅ NEW: Get the most recent journal entries for a user
    public List<JournalEntry> getRecentEntries(String userId, int limit) {
        // Fetch the most recent entries up to the given limit
        return journalEntryRepository.findByUserIdOrderByTimestampDesc(userId, PageRequest.of(0, limit)).getContent();
    }

    // Optional: Save entry (used by controller directly)
    public JournalEntry saveEntry(JournalEntry journalEntry) {
        return journalEntryRepository.save(journalEntry);
    }
}
