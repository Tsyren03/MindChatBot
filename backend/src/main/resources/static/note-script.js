document.addEventListener("DOMContentLoaded", function () {
    const notesList = document.getElementById("notes-list");

    // Load notes on page load
    if (notesList) {
        const noteDate = new URLSearchParams(window.location.search).get('date');
        if (noteDate) {
            fetchNotesForDate(noteDate);
        } else {
            fetchNotesFromBackend();
        }
    }
});

// Fetch all notes
function fetchNotesFromBackend() {
    fetch("/user/notes")
        .then(response => response.json())
        .then(data => {
            const notesList = document.getElementById("notes-list");
            if (!data || data.length === 0) {
                notesList.innerHTML = "<p>No notes available.</p>";
                return;
            }

            notesList.innerHTML = "";
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

// Fetch notes by date
function fetchNotesForDate(date) {
    fetch(`/user/notes?date=${date}`)
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
