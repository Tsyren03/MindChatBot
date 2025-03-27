package MindChatBot.mindChatBot.controller;

import MindChatBot.mindChatBot.model.JournalEntry;
import MindChatBot.mindChatBot.repository.JournalEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
@RestController
@RequestMapping("/api/notes")
public class JournalEntryController {

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @GetMapping
    public List<JournalEntry> getAllNotes(@RequestParam(required = false) String date) {
        if (date != null) {
            LocalDate localDate = LocalDate.parse(date); // parse the date string into LocalDate
            return journalEntryRepository.findByDate(localDate); // use LocalDate directly
        }
        return journalEntryRepository.findAll(); // return all notes if no date is provided
    }

    @PostMapping
    public JournalEntry createNote(@RequestBody JournalEntry journalEntry) {
        if (journalEntry.getDate() == null) {
            journalEntry.setDate(journalEntry.getTimestamp().toLocalDate()); // Set date from timestamp if not set
        }
        return journalEntryRepository.save(journalEntry);
    }
}