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
                    const replyData = await response.json();
                    form.reset();
                    // === Show popup if valid mood recognized ===
                    if (replyData.mood && replyData.mood.main && replyData.mood.sub && replyData.mood.year && replyData.mood.month && replyData.mood.day) {
                        showRecognizedMoodPopup(replyData.mood);
                    }
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
                if (mood.emoji && mood.subMood && MOOD_COLOR_MAP[mood.emoji] && MOOD_COLOR_MAP[mood.emoji][mood.subMood]) {
                    color = MOOD_COLOR_MAP[mood.emoji][mood.subMood];
                } else if (mood.emoji) {
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

    // === 7. Load last 5 chat messages on page load and on first open ===
    loadLastChatMessages();

    // === Show previous chat messages if chatbox is opened (for SPA navigation or first open) ===
    const chatBox = document.getElementById("chat-box");
    if (chatBox) {
        chatBox.addEventListener("focusin", function() {
            // Only reload if chat is empty (prevents duplicate messages)
            const messagesDiv = document.getElementById("messages");
            if (messagesDiv && messagesDiv.children.length === 0) {
                loadLastChatMessages();
            }
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

// === Constant color map for all moods and submoods ===
const MOOD_COLOR_MAP = {
    best: {
        proud:  "#00e6d0",
        grateful: "#00cfff",
        energetic: "#7ee787",
        excited: "#ffe066",
        fulfilled: "#ffb3ff"
    },
    good: {
        calm: "#a7ffeb",
        productive: "#b2f7ef",
        hopeful: "#f6d365",
        motivated: "#fda085",
        friendly: "#ffd6e0"
    },
    neutral: {
        indifferent: "#d1d5db",
        blank: "#e5e7eb",
        tired: "#f3f4f6",
        bored: "#9ca3af",
        quiet: "#6b7280"
    },
    poor: {
        frustrated: "#fca5a5",
        overwhelmed: "#f87171",
        nervous: "#fbbf24",
        insecure: "#f59e42",
        confused: "#f87171"
    },
    bad: {
        angry: "#f87171",
        sad: "#ef4444",
        lonely: "#dc2626",
        anxious: "#b91c1c",
        hopeless: "#991b1b"
    }
};

// === Main mood → submood map (for submood buttons) ===
const MOOD_MAP = {
    best: ["proud", "grateful", "energetic", "excited", "fulfilled"],
    good: ["calm", "productive", "hopeful", "motivated", "friendly"],
    neutral: ["indifferent", "blank", "tired", "bored", "quiet"],
    poor: ["frustrated", "overwhelmed", "nervous", "insecure", "confused"],
    bad: ["angry", "sad", "lonely", "anxious", "hopeless"]
};

window.showSubMoodButtons = function(mainMood) {
    const container = document.getElementById("submood-buttons-container");
    if (!container) return;
    container.innerHTML = '';
    const submoods = MOOD_MAP[mainMood];
    const colors = MOOD_COLOR_MAP[mainMood];
    submoods.forEach((subMood, idx) => {
        const btn = document.createElement('button');
        btn.className = 'submood-btn';
        btn.textContent = subMood.charAt(0).toUpperCase() + subMood.slice(1);
        btn.style.background = colors[subMood] || '#e5e7eb';
        btn.style.color = '#222';
        btn.onclick = function() { saveMoodWithSubMoodLive(mainMood, subMood); };
        container.appendChild(btn);
    });
    container.style.display = 'flex';
    container.style.flexWrap = 'wrap';
    container.style.justifyContent = 'center';
    container.style.marginTop = '12px';
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
    if (emoji && subMood && MOOD_COLOR_MAP[emoji] && MOOD_COLOR_MAP[emoji][subMood]) {
        color = MOOD_COLOR_MAP[emoji][subMood];
    } else if (emoji) {
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

// === Popup for recognized mood confirmation ===
function showRecognizedMoodPopup(moodObj) {
    const oldPopup = document.getElementById("recognized-mood-popup");
    if (oldPopup) oldPopup.remove();
    const popup = document.createElement("div");
    popup.id = "recognized-mood-popup";
    popup.style.position = "fixed";
    popup.style.top = "0";
    popup.style.left = "0";
    popup.style.width = "100vw";
    popup.style.height = "100vh";
    popup.style.background = "rgba(0,0,0,0.35)";
    popup.style.display = "flex";
    popup.style.alignItems = "center";
    popup.style.justifyContent = "center";
    popup.style.zIndex = "9999";
    const box = document.createElement("div");
    box.style.background = "#fff";
    box.style.padding = "32px 28px";
    box.style.borderRadius = "16px";
    box.style.boxShadow = "0 4px 24px rgba(0,0,0,0.18)";
    box.style.textAlign = "center";
    const dateStr = `${moodObj.year}-${String(moodObj.month).padStart(2, '0')}-${String(moodObj.day).padStart(2, '0')}`;
    box.innerHTML = `
        <h3 style='margin-bottom:12px;'>AI Mood Suggestion</h3>
        <div style='font-size:18px;margin-bottom:10px;'>AI recognized your mood for <b>${dateStr}</b> as:</div>
        <div style='font-size:22px;font-weight:bold;margin-bottom:18px;'>${moodObj.main} / ${moodObj.sub}</div>
        <div style='margin-bottom:18px;'>Do you want to save this as your mood for that day?</div>
        <button id="accept-mood-btn" style="margin-right:16px;padding:8px 22px;background:#229ED9;color:#fff;border:none;border-radius:8px;font-weight:600;">Yes, save</button>
        <button id="decline-mood-btn" style="padding:8px 22px;background:#eee;color:#333;border:none;border-radius:8px;font-weight:600;">No, cancel</button>
    `;
    popup.appendChild(box);
    document.body.appendChild(popup);
    document.getElementById("accept-mood-btn").onclick = async function() {
        const { token } = getUserIdFromToken();
        const mood = {
            year: moodObj.year,
            month: moodObj.month,
            day: moodObj.day,
            emoji: moodObj.main,
            subMood: moodObj.sub
        };
        const response = await fetch('/user/moods/save', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                ...(token && { "Authorization": `Bearer ${token}` })
            },
            body: JSON.stringify(mood)
        });
        if (response.ok) {
            // === 캘린더 셀 색상 즉시 변경 ===
            const currentMonth = parseInt(document.getElementById("current-month").dataset.month);
            const currentYear = parseInt(document.getElementById("current-month").dataset.year);
            if (mood.year === currentYear && mood.month - 1 === currentMonth) {
                const calendar = document.getElementById("calendar");
                if (calendar) {
                    const dayElements = calendar.querySelectorAll(".calendar-day");
                    dayElements.forEach(el => {
                        if (el.dataset.day == mood.day) {
                            let color = '';
                            if (mood.emoji && mood.subMood && MOOD_COLOR_MAP[mood.emoji] && MOOD_COLOR_MAP[mood.emoji][mood.subMood]) {
                                color = MOOD_COLOR_MAP[mood.emoji][mood.subMood];
                            } else if (mood.emoji) {
                                const colorMap = {
                                    bad: '#ff4d4d',
                                    poor: '#ffa500',
                                    neutral: '#d3d3d3',
                                    good: '#90ee90',
                                    best: '#32cd32'
                                };
                                color = colorMap[mood.emoji] || '';
                            }
                            el.style.backgroundColor = color;
                            el.title = mood.emoji + (mood.subMood ? (': ' + mood.subMood) : '');
                        }
                    });
                }
            }
            // === 챗봇 응답 출력 ===
            const replyData = await response.json();
            const replyText = replyData.reply || "Your mood has been saved.";
            const chatReplyEl = document.getElementById("chat-reply");
            if (chatReplyEl) {
                chatReplyEl.textContent = replyText;
            }
            addMessage("bot", replyText);
        }
        popup.remove();
    };
    document.getElementById("decline-mood-btn").onclick = function() {
        popup.remove();
    };
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
    messageElement.textContent = text;
    messagesDiv.appendChild(messageElement);
    messagesDiv.scrollTop = messagesDiv.scrollHeight;
    saveMessageToLocal(sender, text);
}

function saveMessageToLocal(sender, text) {
    let arr = [];
    try {
        arr = JSON.parse(localStorage.getItem('chatHistory') || '[]');
    } catch {}
    arr.push({ sender, text });
    if (arr.length > 5) arr = arr.slice(arr.length - 5);
    localStorage.setItem('chatHistory', JSON.stringify(arr));
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

// === 캘린더 셀 색상 즉시 변경 함수 ===
function updateCalendarCellColor(year, month, day, emoji, subMood) {
    // 현재 달만 반영
    const currentMonth = parseInt(document.getElementById("current-month").dataset.month);
    const currentYear = parseInt(document.getElementById("current-month").dataset.year);
    if (year !== currentYear || month - 1 !== currentMonth) return;
    const calendar = document.getElementById("calendar");
    if (!calendar) return;
    const dayElements = calendar.querySelectorAll(".calendar-day");
    dayElements.forEach(el => {
        if (el.dataset.day == day) {
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
            el.style.backgroundColor = color;
            el.title = emoji + (subMood ? (': ' + subMood) : '');
        }
    });
}

// === Load last 5 chat messages from localStorage (fallback if no backend) ===
function loadLastChatMessages() {
    // 1. Try backend API first
    const { userId, token } = getUserIdFromToken();
    fetch('/api/chat/history?limit=5', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            ...(token && { "Authorization": `Bearer ${token}` })
        }
    })
    .then(res => {
        if (res.ok) return res.json();
        throw new Error('No backend');
    })
    .then(messages => {
        if (Array.isArray(messages) && messages.length > 0) {
            const messagesDiv = document.getElementById("messages");
            messagesDiv.innerHTML = '';
            messages.forEach(msg => {
                if (msg.sender && msg.text) {
                    addMessage(msg.sender, msg.text);
                }
            });
        } else {
            loadLastChatMessagesFromLocal();
        }
    })
    .catch(() => {
        loadLastChatMessagesFromLocal();
    });
}

function loadLastChatMessagesFromLocal() {
    let arr = [];
    try {
        arr = JSON.parse(localStorage.getItem('chatHistory') || '[]');
    } catch {}
    const messagesDiv = document.getElementById("messages");
    messagesDiv.innerHTML = '';
    arr.forEach(msg => {
        if (msg.sender && msg.text) {
            addMessage(msg.sender, msg.text);
        }
    });
}
