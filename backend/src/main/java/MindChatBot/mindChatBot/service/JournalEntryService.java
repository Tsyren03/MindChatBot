package MindChatBot.mindChatBot.service;

import MindChatBot.mindChatBot.model.JournalEntry;
import MindChatBot.mindChatBot.repository.JournalEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JournalEntryService {

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    public List<JournalEntry> findAllEntries() {
        return journalEntryRepository.findAll();
    }

    public JournalEntry saveEntry(JournalEntry entry) {
        return journalEntryRepository.save(entry);
    }

    public void deleteEntry(String id) {
        journalEntryRepository.deleteById(id);
    }
}
