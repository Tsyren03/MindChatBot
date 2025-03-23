package MindChatBot.mindChatBot.controller;

import MindChatBot.mindChatBot.model.JournalEntry;
import MindChatBot.mindChatBot.repository.JournalEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class JournalEntryController {

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @GetMapping
    public List<JournalEntry> getAllNotes(@RequestParam(required = false) String date) {
        if (date != null) {
            return journalEntryRepository.findByDate(date);
        }
        return journalEntryRepository.findAll();
    }

    @PostMapping
    public JournalEntry createNote(@RequestBody JournalEntry journalEntry) {
        if (journalEntry.getDate() == null) {
            journalEntry.setDate(journalEntry.getTimestamp().toLocalDate());
        }
        return journalEntryRepository.save(journalEntry);
    }
}