document.addEventListener("DOMContentLoaded", function () {
    const newNoteForm = document.getElementById("new-note-form");
    const notesList = document.getElementById("notes-list");

    if (newNoteForm) {
        newNoteForm.addEventListener("submit", function (event) {
            event.preventDefault();
            const noteContent = document.getElementById("note-content").value;
            const noteDate = document.getElementById("note-date").value;

            saveNoteToBackend(noteContent, noteDate);
        });
    }

    if (notesList) {
        const noteDate = new URLSearchParams(window.location.search).get('date');
        if (noteDate) {
            fetchNotesForDate(noteDate);
        } else {
            fetchNotesFromBackend();
        }
    }
});

function saveNoteToBackend(content, date) {
    fetch("/api/notes", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ content, date, userId: "defaultUser", timestamp: new Date().toISOString() })
    })
    .then(response => response.json())
    .then(() => {
        alert("Note saved successfully!");
        window.location.href = "notes.html";
    })
    .catch(error => {
        console.error("Error saving note:", error);
    });
}

function fetchNotesFromBackend() {
    fetch("/api/notes")
        .then(response => response.json())
        .then(data => {
            const notesList = document.getElementById("notes-list");
            data.forEach(note => {
                const noteDiv = document.createElement("div");
                noteDiv.classList.add("note");
                noteDiv.textContent = `${note.date}: ${note.content}`;
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

