package MindChatBot.mindChatBot.controller;

import MindChatBot.mindChatBot.model.JournalEntry;
import MindChatBot.mindChatBot.service.JournalEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/user/notes")
public class JournalEntryController {

    private final JournalEntryService journalEntryService;

    @Autowired
    public JournalEntryController(JournalEntryService journalEntryService) {
        this.journalEntryService = journalEntryService;
    }

    // ✅ 1. Get all notes or filter by date
    @GetMapping
    public List<JournalEntry> getNotes(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        // Retrieve the currently authenticated user's ID
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        if (date != null) {
            // Return notes for the specific date
            return journalEntryService.getEntriesForUserByDate(userId, date);
        } else {
            // Return all notes for the user
            return journalEntryService.getAllEntriesForUser(userId);
        }
    }

    // ✅ 2. Create a new journal entry (note)
    @PostMapping
    public JournalEntry createNote(@RequestBody JournalEntry note) {
        // Ensure the userId is properly set before saving
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        // Log the creation attempt
        System.out.println("Creating note for user: " + userId); // Add logging for debugging

        // Set the user ID for the note
        note.setUserId(userId);

        // Set the current timestamp for the note
        note.setTimestamp(LocalDateTime.now());

        // Log the note data to verify the content before saving
        System.out.println("Saving note: " + note.toString());

        // Save the note through the service layer
        return journalEntryService.saveEntry(note);
    }
}
