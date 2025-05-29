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

    function clearSubMoodUI() {
        const container = document.getElementById("submood-buttons-container");
        if (container) container.style.display = 'none';
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
                let color = '';
                if (mood.emoji && mood.subMood) {
                    const submoods = MOOD_MAP[mood.emoji];
                    const colors = SUBMOOD_COLOR[mood.emoji];
                    const idx = submoods ? submoods.indexOf(mood.subMood) : -1;
                    if (colors && idx >= 0) color = colors[idx];
                }
                if (!color && mood.emoji) {
                    const colorMap = {
                        bad: '#ff4d4d',
                        poor: '#ffa500',
                        neutral: '#d3d3d3',
                        good: '#90ee90',
                        best: '#32cd32'
                    };
                    color = colorMap[mood.emoji] || '';
                }
                dayElement.style.backgroundColor = color;
                dayElement.title = mood.emoji + (mood.subMood ? (': ' + mood.subMood) : '');
            }

            dayElement.addEventListener("click", () => {
                selectDay(dayElement);
                clearSubMoodUI();
            });
            calendar.appendChild(dayElement);
        }
    }

    prevMonthButton?.addEventListener("click", () => {
        currentMonth--;
        if (currentMonth < 0) { currentMonth = 11; currentYear--; }
        updateCalendar();
        clearSubMoodUI();
    });

    nextMonthButton?.addEventListener("click", () => {
        currentMonth++;
        if (currentMonth > 11) { currentMonth = 0; currentYear++; }
        updateCalendar();
        clearSubMoodUI();
    });

    currentDateElement.textContent = `Today: ${new Date().toDateString()}`;
    updateCalendar();

    // === 6. Send All Logs and Moods Button ===
    const sendAllButton = document.getElementById("send-all-button");
    if (sendAllButton) {
        sendAllButton.addEventListener("click", function (e) {
            e.preventDefault();
            sendAllLogsAndMoods();
        });
    }
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

// === SubMood Map ===
const MOOD_MAP = {
    best: ["proud", "grateful", "energetic", "excited", "fulfilled"],
    good: ["calm", "productive", "hopeful", "motivated", "friendly"],
    neutral: ["indifferent", "blank", "tired", "bored", "quiet"],
    poor: ["frustrated", "overwhelmed", "nervous", "insecure", "confused"],
    bad: ["angry", "sad", "lonely", "anxious", "hopeless"]
};

const SUBMOOD_COLOR = {
    best: [
        "#00e6d0", // 밝은 청록
        "#00cfff", // 밝은 하늘색
        "#7ee787", // 밝은 연두
        "#ffe066", // 밝은 노랑
        "#ffb3ff"  // 밝은 핑크
    ],
    good: [
        "#a7ffeb", // 밝은 민트
        "#b2f7ef", // 밝은 하늘민트
        "#f6d365", // 밝은 노랑
        "#fda085", // 밝은 오렌지
        "#ffd6e0"  // 밝은 핑크
    ],
    neutral: ["#d1d5db", "#e5e7eb", "#f3f4f6", "#9ca3af", "#6b7280"],
    poor: ["#fca5a5", "#f87171", "#fbbf24", "#f59e42", "#f87171"],
    bad: ["#f87171", "#ef4444", "#dc2626", "#b91c1c", "#991b1b"]
};

window.showSubMoodButtons = function(mainMood) {
    const container = document.getElementById("submood-buttons-container");
    if (!container) return;
    container.innerHTML = '';
    const submoods = MOOD_MAP[mainMood];
    const colors = SUBMOOD_COLOR[mainMood];
    submoods.forEach((subMood, idx) => {
        const btn = document.createElement('button');
        btn.className = 'submood-btn';
        btn.textContent = subMood.charAt(0).toUpperCase() + subMood.slice(1);
        btn.style.background = colors[idx] || '#e5e7eb';
        btn.style.color = '#222';
        btn.style.margin = '0 8px 8px 0';
        btn.style.padding = '10px 18px';
        btn.style.border = 'none';
        btn.style.borderRadius = '8px';
        btn.style.fontWeight = '600';
        btn.style.fontSize = '15px';
        btn.style.cursor = 'pointer';
        btn.onclick = function() { saveMoodWithSubMoodLive(mainMood, subMood); };
        container.appendChild(btn);
    });
    container.style.display = 'flex';
    container.style.flexWrap = 'wrap';
    container.style.justifyContent = 'center';
};

async function saveMoodWithSubMoodLive(emoji, subMood) {
    const selectedDayElement = document.querySelector(".calendar-day.selected");
    if (!selectedDayElement) {
        alert("Please select a date first.");
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
        emoji,
        subMood
    };
    // 즉시 submood 색상 반영
    let color = '';
    if (emoji && subMood) {
        const submoods = MOOD_MAP[emoji];
        const colors = SUBMOOD_COLOR[emoji];
        const idx = submoods ? submoods.indexOf(subMood) : -1;
        if (colors && idx >= 0) color = colors[idx];
    }
    if (!color && emoji) {
        const colorMap = {
            bad: '#ff4d4d',
            poor: '#ffa500',
            neutral: '#d3d3d3',
            good: '#90ee90',
            best: '#32cd32'
        };
        color = colorMap[emoji] || '';
    }
    selectedDayElement.style.backgroundColor = color;
    selectedDayElement.title = emoji + (subMood ? (': ' + subMood) : '');
    // 서버 저장
    const response = await fetch('/user/moods/save', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            ...(token && { "Authorization": `Bearer ${token}` })
        },
        body: JSON.stringify(mood)
    });

    if (response.ok) {
        const replyData = await response.json();
        const replyText = replyData.reply || "Your mood has been saved.";

        const chatReplyEl = document.getElementById("chat-reply");
        if (chatReplyEl) {
            chatReplyEl.textContent = replyText;
        }
        // 챗봇 박스에 바로 출력
        addMessage("bot", replyText);
    } else {
        alert("Failed to save mood.");
    }
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
    // 텔레그램 스타일: 말풍선에서 닉네임 없이 메시지만 표시
    messageElement.textContent = text;
    messagesDiv.appendChild(messageElement);
    messagesDiv.scrollTop = messagesDiv.scrollHeight;
}

// === 6. Send All Logs and Moods ===
async function sendAllLogsAndMoods() {
    try {
        const userId = "Xizel.03@gmail.com"; // default or test user

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

