document.addEventListener("DOMContentLoaded", function () {
    const newNoteForm = document.getElementById("new-note-form");
    const notesList = document.getElementById("notes-list");

    // If the form exists, add an event listener to handle form submissions
    if (newNoteForm) {
        newNoteForm.addEventListener("submit", function (event) {
            event.preventDefault();
            const noteContent = document.getElementById("note-content").value;
            const noteDate = document.getElementById("note-date").value;

            // Ensure the note date is not empty
            if (!noteContent || !noteDate) {
                alert("Both content and date are required.");
                return;
            }

            // Format the date to always be in English (en-US)
            const formattedDate = new Date(noteDate).toLocaleDateString('en-US', {
                weekday: 'long',
                year: 'numeric',
                month: 'long',
                day: 'numeric'
            });

            // Call the function to save the note to the backend
            saveNoteToBackend(noteContent, formattedDate);
        });
    }

    // Fetch notes when the page loads
    if (notesList) {
        const noteDate = new URLSearchParams(window.location.search).get('date');
        if (noteDate) {
            fetchNotesForDate(noteDate);
        } else {
            fetchNotesFromBackend();
        }
    }
});

// Function to save a new note to the backend
function saveNoteToBackend(content, date) {
    fetch("/api/notes", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ content, date, userId: "defaultUser", timestamp: new Date().toISOString() })
    })
    .then(response => response.json())
    .then(data => {
        // Check if the backend returned a valid note
        if (data && data.success) {
            alert("Note saved successfully!");
            // After saving the note, refresh the list of notes
            fetchNotesFromBackend();
        } else {
            alert("Failed to save the note.");
        }
    })
    .catch(error => {
        console.error("Error saving note:", error);
        alert("An error occurred while saving the note.");
    });
}

// Function to fetch and display notes from the backend
function fetchNotesFromBackend() {
    fetch("/api/notes")
        .then(response => response.json())
        .then(data => {
            const notesList = document.getElementById("notes-list");
            if (data.length === 0) {
                notesList.innerHTML = "<p>No notes available.</p>"; // If no notes exist
                return;
            }

            // Clear existing notes list
            notesList.innerHTML = "";

            // Loop through the fetched notes and display each one
            data.forEach(note => {
                const formattedDate = new Date(note.date).toLocaleDateString('en-US', {
                    weekday: 'long',
                    year: 'numeric',
                    month: 'long',
                    day: 'numeric'
                });

                const noteDiv = document.createElement("div");
                noteDiv.classList.add("note");
                noteDiv.innerHTML = `<p><strong>${formattedDate}</strong></p><p>${note.content}</p>`;
                notesList.appendChild(noteDiv);
            });
        })
        .catch(error => {
            console.error("Error fetching notes:", error);
        });
}

function fetchNotesForDate(date) {
    fetch(`/api/notes?date=${date}`)
        .then(response => response.json())
        .then(data => {
            const notesList = document.getElementById("notes-list");
            notesList.innerHTML = "";
            if (data.length > 0) {
                data.forEach(note => {
                    const noteDiv = document.createElement("div");
                    noteDiv.classList.add("note");
                    noteDiv.textContent = `${note.date}: ${note.content}`;
                    notesList.appendChild(noteDiv);
                });
            } else {
                notesList.textContent = "No notes for this date.";
            }
        })
        .catch(error => {
            console.error("Error fetching notes:", error);
        });
}

