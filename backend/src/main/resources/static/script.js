document.addEventListener("DOMContentLoaded", function () {
    // === 1. Note Form Submission ===
    const form = document.getElementById("new-note-form");

    if (form) {
        form.addEventListener("submit", async function (event) {
            event.preventDefault();

            const content = document.getElementById("note-content").value;
            const date = document.getElementById("note-date").value;
            const { token } = getUserIdFromToken();

            try {
                const response = await fetch("/user/notes", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        ...(token && { "Authorization": `Bearer ${token}` })
                    },
                    body: JSON.stringify({ content, date })
                });

                if (response.ok) {
                    alert("Note saved successfully!");
                    form.reset();
                } else {
                    alert("Failed to save note.");
                }
            } catch (error) {
                console.error("Error:", error);
                alert("An error occurred while saving the note.");
            }
        });
    }

    // === 2. Calendar Rendering ===
    const calendar = document.getElementById("calendar");
    const currentDateElement = document.getElementById("current-date");
    const prevMonthButton = document.getElementById("prev-month");
    const nextMonthButton = document.getElementById("next-month");
    const currentMonthElement = document.getElementById("current-month");

    let currentYear = new Date().getFullYear();
    let currentMonth = new Date().getMonth();

    async function fetchMoods(year, month) {
        const { token } = getUserIdFromToken();
        const response = await fetch('/user/moods/fetch', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                ...(token && { "Authorization": `Bearer ${token}` })
            },
            body: JSON.stringify({ year, month: month + 1 })
        });
        return response.json();
    }

    async function updateCalendar() {
        calendar.innerHTML = "";
        currentMonthElement.textContent = new Date(currentYear, currentMonth)
            .toLocaleString('en-US', { month: 'long', year: 'numeric' });
        currentMonthElement.dataset.year = currentYear;
        currentMonthElement.dataset.month = currentMonth;

        const firstDayOfMonth = new Date(currentYear, currentMonth, 1);
        const startDay = (firstDayOfMonth.getDay() + 6) % 7;
        const daysInMonth = new Date(currentYear, currentMonth + 1, 0).getDate();
        const savedMoods = await fetchMoods(currentYear, currentMonth);

        for (let i = 0; i < startDay; i++) {
            const emptyCell = document.createElement("div");
            emptyCell.classList.add("calendar-day", "empty");
            calendar.appendChild(emptyCell);
        }

        for (let day = 1; day <= daysInMonth; day++) {
            const dayElement = document.createElement("div");
            dayElement.classList.add("calendar-day");
            dayElement.textContent = day;
            dayElement.dataset.day = day;

            const mood = savedMoods.find(m => m.day === day);
            if (mood) {
                const colorMap = {
                    bad: '#ff4d4d',
                    poor: '#ffa500',
                    neutral: '#d3d3d3',
                    good: '#90ee90',
                    best: '#32cd32'
                };
                dayElement.style.backgroundColor = colorMap[mood.emoji] || '';
            }

            dayElement.addEventListener("click", () => selectDay(dayElement));
            calendar.appendChild(dayElement);
        }
    }

    prevMonthButton?.addEventListener("click", () => {
        currentMonth--;
        if (currentMonth < 0) { currentMonth = 11; currentYear--; }
        updateCalendar();
    });

    nextMonthButton?.addEventListener("click", () => {
        currentMonth++;
        if (currentMonth > 11) { currentMonth = 0; currentYear++; }
        updateCalendar();
    });

    currentDateElement.textContent = `Today: ${new Date().toDateString()}`;
    updateCalendar();
});

// === 3. Select Day ===
function selectDay(dayElement) {
    document.querySelectorAll(".calendar-day").forEach(el => el.classList.remove("selected"));
    dayElement.classList.add("selected");

    const noteDateInput = document.getElementById("note-date");
    if (noteDateInput) {
        const selectedDate = new Date();
        selectedDate.setFullYear(
            parseInt(document.getElementById("current-month").dataset.year),
            parseInt(document.getElementById("current-month").dataset.month),
            dayElement.dataset.day
        );
        noteDateInput.value = selectedDate.toISOString().split('T')[0];
    }
}

// === 4. Set Mood ===
async function setMood(moodValue) {
    const selectedDayElement = document.querySelector(".calendar-day.selected");
    if (!selectedDayElement) {
        alert("Please select a date first!");
        return;
    }

    const { token } = getUserIdFromToken();
    const day = selectedDayElement.dataset.day;
    const year = parseInt(document.getElementById("current-month").dataset.year);
    const month = parseInt(document.getElementById("current-month").dataset.month);

    const mood = {
        year,
        month: month + 1,
        day: parseInt(day),
        emoji: moodValue
    };

    await fetch('/user/moods/save', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            ...(token && { "Authorization": `Bearer ${token}` })
        },
        body: JSON.stringify(mood)
    });

    const colorMap = {
        bad: '#ff4d4d',
        poor: '#ffa500',
        neutral: '#d3d3d3',
        good: '#90ee90',
        best: '#32cd32'
    };
    selectedDayElement.style.backgroundColor = colorMap[moodValue] || '';
}

// === 5. Chat Message Handling ===
async function sendMessage() {
    const input = document.getElementById("user-input");
    const message = input.value.trim();
    if (message === "") return;

    addMessage("user", message);
    input.value = "";

    const { userId, token } = getUserIdFromToken();

    try {
        const response = await fetch("/api/chat", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                ...(token && { "Authorization": `Bearer ${token}` })
            },
            body: JSON.stringify({ message, userId })
        });

        const data = await response.json();
        if (data.response) {
            addMessage("bot", data.response);
        } else {
            addMessage("bot", "⚠️ " + (data.error || "Unknown server response."));
        }
    } catch (error) {
        console.error("Error sending message:", error);
        addMessage("bot", "⚠️ Server error occurred.");
    }
}

function getUserIdFromToken() {
    const token = localStorage.getItem("authToken");
    if (!token) return { userId: "anonymous", token: null };

    try {
        const decoded = JSON.parse(atob(token.split('.')[1]));
        const userId = decoded.userId || decoded.sub || decoded.email || decoded.username || "anonymous";
        return { userId, token };
    } catch (error) {
        console.error("Error decoding token:", error);
        localStorage.removeItem("authToken");
        return { userId: "anonymous", token: null };
    }
}

function addMessage(sender, text) {
    const messagesDiv = document.getElementById("messages");
    const messageElement = document.createElement("div");
    messageElement.classList.add("message", sender);
    messageElement.textContent = `${sender === "user" ? "You" : "Bot"}: ${text}`;
    messagesDiv.appendChild(messageElement);
    messagesDiv.scrollTop = messagesDiv.scrollHeight;
}
async function sendAllLogsAndMoods() {
    try {
        // Remove the authentication check
        const userId = "Xizel.03@gmail.com"; // Assuming anonymous user if no login is required

        addMessage("bot", "🔄 Analyzing mood patterns and journal entries...");

        const response = await fetch("/api/chat/sendAllData", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ userId })
        });

        const result = await response.json();

        if (result.response) {
            addMessage("bot", result.response);
        } else {
            addMessage("bot", "⚠️ Error: " + (result.error || "No insights could be generated"));
        }
    } catch (error) {
        console.error("Error:", error);
        addMessage("bot", "⚠️ Failed to analyze data: " + error.message);
    }
}
document.addEventListener("DOMContentLoaded", function () {
    const sendAllButton = document.getElementById("send-all-button");
    if (sendAllButton) {
        sendAllButton.addEventListener("click", function (e) {
            e.preventDefault();
            sendAllLogsAndMoods();
        });
    }
});
