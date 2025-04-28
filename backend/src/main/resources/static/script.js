// 이 코드를 script.js 파일에 추가
document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("new-note-form");

    form.addEventListener("submit", function (event) {
        event.preventDefault(); // 기본 폼 제출 막기

        const content = document.getElementById("note-content").value;
        const date = document.getElementById("note-date").value;

        fetch("/user/notes", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                content: content,
                date: date
            })
        })
        .then(response => {
            if (response.ok) {
                alert("Note saved successfully!");
                form.reset(); // 폼 초기화
            } else {
                alert("Failed to save note.");
            }
        })
        .catch(error => {
            console.error("Error:", error);
            alert("An error occurred while saving the note.");
        });
    });
});
// Calendar rendering
document.addEventListener("DOMContentLoaded", function () {
    const calendar = document.getElementById("calendar");
    const currentDateElement = document.getElementById("current-date");
    const prevMonthButton = document.getElementById("prev-month");
    const nextMonthButton = document.getElementById("next-month");
    const currentMonthElement = document.getElementById("current-month");

    let currentYear = new Date().getFullYear();
    let currentMonth = new Date().getMonth();
    async function fetchMoods(year, month) {
        const response = await fetch('/user/moods/fetch', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ year: year, month: month + 1 })
        });
        return response.json();
    }

    async function saveMood(mood) {
        const response = await fetch('/user/moods/save', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(mood)
        });
        return response.json();
    }

    async function fetchMoodStats() {
        const response = await fetch('/user/moods/stats');
        return response.json();
    }
    async function updateCalendar() {
        calendar.innerHTML = "";

        currentMonthElement.textContent = new Date(currentYear, currentMonth)
            .toLocaleString('en-US', { month: 'long', year: 'numeric' });

        const firstDayOfMonth = new Date(currentYear, currentMonth, 1);
        const startDay = (firstDayOfMonth.getDay() + 6) % 7;
        const daysInMonth = new Date(currentYear, currentMonth + 1, 0).getDate();
        const savedMoods = await fetchMoods(currentYear, currentMonth);

        for (let i = 0; i < startDay; i++) {
            const emptyCell = document.createElement("div");
            emptyCell.classList.add("calendar-day", "empty");
            calendar.appendChild(emptyCell);
        }

        // Add actual day elements
        for (let day = 1; day <= daysInMonth; day++) {
            const dayElement = document.createElement("div");
            dayElement.classList.add("calendar-day");
            dayElement.textContent = day;
            dayElement.dataset.day = day;

            // Apply color based on mood
            const mood = savedMoods.find(m => m.day === day);
            if (mood) {
                switch (mood.emoji) {
                    case 'bad': dayElement.style.backgroundColor = '#ff4d4d'; break;
                    case 'poor': dayElement.style.backgroundColor = '#ffa500'; break;
                    case 'neutral': dayElement.style.backgroundColor = '#d3d3d3'; break;
                    case 'good': dayElement.style.backgroundColor = '#90ee90'; break;
                    case 'best': dayElement.style.backgroundColor = '#32cd32'; break;
                }
            }

            dayElement.addEventListener("click", () => selectDay(dayElement));
            calendar.appendChild(dayElement);
        }
    }

    prevMonthButton.addEventListener("click", () => {
        currentMonth--;
        if (currentMonth < 0) { currentMonth = 11; currentYear--; }
        updateCalendar();
    });

    nextMonthButton.addEventListener("click", () => {
        currentMonth++;
        if (currentMonth > 11) { currentMonth = 0; currentYear++; }
        updateCalendar();
    });

    currentDateElement.textContent = `Today: ${new Date().toDateString()}`;
    updateCalendar();
});

// Select a day
function selectDay(dayElement) {
    document.querySelectorAll(".calendar-day").forEach(el => el.classList.remove("selected"));
    dayElement.classList.add("selected");

    const noteDateInput = document.getElementById("note-date");
    if (noteDateInput) {
        const selectedDate = new Date();
        selectedDate.setFullYear(currentYear, currentMonth, dayElement.dataset.day);
        noteDateInput.value = selectedDate.toISOString().split('T')[0];
    }
}

// Set Mood
async function setMood(moodValue) {
    const selectedDayElement = document.querySelector(".calendar-day.selected");
    if (!selectedDayElement) {
        alert("Please select a date first!");
        return;
    }

    const day = selectedDayElement.dataset.day;
    const currentYear = new Date().getFullYear();
    const currentMonth = new Date().getMonth();

    const mood = {
        year: currentYear,
        month: currentMonth + 1,
        day: parseInt(day),
        emoji: moodValue
    };

    await fetch('/user/moods/save', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(mood)
    });
    switch (moodValue) {
        case 'bad': selectedDayElement.style.backgroundColor = '#ff4d4d'; break;
        case 'poor': selectedDayElement.style.backgroundColor = '#ffa500'; break;
        case 'neutral': selectedDayElement.style.backgroundColor = '#d3d3d3'; break;
        case 'good': selectedDayElement.style.backgroundColor = '#90ee90'; break;
        case 'best': selectedDayElement.style.backgroundColor = '#32cd32'; break;
    }
}
async function sendMessage() {
     const input = document.getElementById("user-input");
     const message = input.value.trim();

     if (message === "") return;

     addMessage("user", message);
     input.value = "";

     const token = localStorage.getItem("authToken");
     let userId = "anonymous";

     if (token) {
         try {
             const decodedToken = JSON.parse(atob(token.split('.')[1]));
             userId = decodedToken.userId;
         } catch (error) {
             console.error("Error decoding token:", error);
             localStorage.removeItem("authToken");
             userId = "anonymous";
         }
     }

     try {
         const response = await fetch("/api/chat", {
             method: "POST",
             headers: { "Content-Type": "application/json" },
             body: JSON.stringify({ message: message, userId: userId })
         });

         const data = await response.json();
         console.log("API Response:", data);

         if (data.response) {
             addMessage("bot", data.response);
         } else if (data.error) {
             addMessage("bot", "⚠️ Error: " + data.error);
         } else {
             addMessage("bot", "⚠️ Unknown response from server.");
         }
     } catch (error) {
         console.error("Error sending message:", error);
         addMessage("bot", "⚠️ Server error occurred.");
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
