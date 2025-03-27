document.addEventListener("DOMContentLoaded", function () {
    const calendar = document.getElementById("calendar");
    const currentDateDiv = document.getElementById("current-date");
    const notesOnDateDiv = document.getElementById("notes-on-date");
    const currentMonthYear = document.getElementById("current-month-year");
    const prevMonthBtn = document.getElementById("prev-month");
    const nextMonthBtn = document.getElementById("next-month");
    const moodButtons = document.querySelectorAll(".mood-button");
    const deleteButton = document.getElementById("delete-log-btn");  // Assume there's a delete button for the selected log
    const moodEmojiDiv = document.getElementById("mood-emoji");  // Assume there's an element to show the mood emoji

    let currentDate = new Date();

    function renderCalendar(date) {
        const year = date.getFullYear();
        const month = date.getMonth();
        currentMonthYear.textContent = `${date.toLocaleString('default', { month: 'long' })} ${year}`;

        const firstDayOfMonth = new Date(year, month, 1).getDay();
        const lastDateOfMonth = new Date(year, month + 1, 0).getDate();

        calendar.innerHTML = "";

        for (let i = 0; i < firstDayOfMonth; i++) {
            const emptyCell = document.createElement("div");
            emptyCell.classList.add("calendar-day");
            calendar.appendChild(emptyCell);
        }

        for (let i = 1; i <= lastDateOfMonth; i++) {
            const dayCell = document.createElement("div");
            dayCell.classList.add("calendar-day");
            dayCell.textContent = i;
            dayCell.dataset.date = `${year}-${String(month + 1).padStart(2, '0')}-${String(i).padStart(2, '0')}`;
            calendar.appendChild(dayCell);

            // Fetch mood for the date and set color
            fetchMoodForDate(dayCell.dataset.date, dayCell);
        }
    }
    function fetchMoodForDate(date, dayCell) {
        fetch(`/api/emotion-logs/date?date=${date}`)
            .then(response => response.json())
            .then(data => {
                if (data && data.length > 0) {
                    const log = data[0];  // Assuming only one log per date
                    switch (log.emotionScore) {
                        case 1:
                            dayCell.style.backgroundColor = "yellow";  // happy
                            break;
                        case -1:
                            dayCell.style.backgroundColor = "blue";  // sad
                            break;
                        case 2:
                            dayCell.style.backgroundColor = "red";  // angry
                            break;
                        case 0:
                            dayCell.style.backgroundColor = "gray";  // neutral
                            break;
                        default:
                            dayCell.style.backgroundColor = "white";
                    }
                }
            })
            .catch(error => {
                console.error("Error fetching mood:", error);
            });
    }

    prevMonthBtn.addEventListener("click", function () {
        currentDate.setMonth(currentDate.getMonth() - 1);
        renderCalendar(currentDate);
    });

    nextMonthBtn.addEventListener("click", function () {
        currentDate.setMonth(currentDate.getMonth() + 1);
        renderCalendar(currentDate);
    });

    if (calendar) {
        calendar.addEventListener("click", function (event) {
            if (event.target.classList.contains("calendar-day") && event.target.dataset.date) {
                const selectedDate = event.target.dataset.date;
                currentDateDiv.textContent = `Selected Date: ${selectedDate}`;
                fetchNotesForDate(selectedDate);
                deleteButton.style.display = "inline";  // Show the delete button when a log is selected
            }
        });
    }

    function fetchNotesForDate(date) {
        fetch(`/api/notes?date=${date}`)
            .then(response => response.json())
            .then(data => {
                notesOnDateDiv.innerHTML = "";
                if (data.length > 0) {
                    data.forEach(note => {
                        const noteDiv = document.createElement("div");
                        noteDiv.classList.add("note");
                        noteDiv.textContent = note.content;
                        notesOnDateDiv.appendChild(noteDiv);
                    });
                } else {
                    notesOnDateDiv.textContent = "No notes for this date.";
                }
            })
            .catch(error => {
                console.error("Error fetching notes:", error);
            });
    }
    function createEmotionLog(moodScore) {
        const selectedDate = currentDateDiv.textContent.split(": ")[1];
        if (selectedDate) {
            const newLog = {
                userId: "user123",  // You can replace this with the actual user ID
                date: selectedDate,
                emotionScore: moodScore,
                aiInsights: "Generated insights based on mood."  // Placeholder for AI insights
            };

            fetch("/api/emotion-logs", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(newLog)
            })
            .then(response => response.json())
            .then(data => {
                alert("Mood log saved successfully!");
                displayMoodEmoji(moodScore);
                fetchMoodForDate(selectedDate, document.querySelector(`[data-date='${selectedDate}']`));
            })
            .catch(error => {
                console.error("Error saving log:", error);
            });
        } else {
            alert("Please select a date first.");
        }
    }

    function displayMoodEmoji(moodScore) {
        let emoji;
        switch (moodScore) {
            case 1:
                emoji = "😊";  // happy
                break;
            case -1:
                emoji = "😢";  // sad
                break;
            case 2:
                emoji = "😠";  // angry
                break;
            case 0:
                emoji = "😐";  // neutral
                break;
            default:
                emoji = "😐";  // default neutral
        }
        moodEmojiDiv.textContent = `Mood: ${emoji}`;
    }

    // Mood buttons for saving a mood log
    moodButtons.forEach(button => {
        button.addEventListener("click", function () {
            const mood = this.dataset.mood;
            const moodScore = parseInt(mood);
            createEmotionLog(moodScore);
        });
    });

    // Delete log button for selected date
    deleteButton.addEventListener("click", function () {
        const selectedDate = currentDateDiv.textContent.split(": ")[1];
        if (selectedDate) {
            fetch(`/api/emotion-logs/date?date=${selectedDate}`, {
                method: "DELETE"
            })
            .then(() => {
                alert("Log deleted successfully!");
                fetchMoodForDate(selectedDate, document.querySelector(`[data-date='${selectedDate}']`));
                notesOnDateDiv.textContent = "No logs for this date.";  // Clear the notes area
                deleteButton.style.display = "none";  // Hide delete button after deleting the log
            })
            .catch(error => {
                console.error("Error deleting log:", error);
            });
        }
    });

    renderCalendar(currentDate);
});
