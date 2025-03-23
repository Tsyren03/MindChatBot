document.addEventListener("DOMContentLoaded", function () {
    const calendar = document.getElementById("calendar");
    const currentDateElement = document.getElementById("current-date");

    // 오늘 날짜 표시
    const today = new Date();
    currentDateElement.textContent = `Today: ${today.toDateString()}`;

    // 저장된 감정 불러오기
    let savedMoods = JSON.parse(localStorage.getItem("moods")) || {};

    // 캘린더 생성
    const daysInMonth = new Date(today.getFullYear(), today.getMonth() + 1, 0).getDate();
    for (let day = 1; day <= daysInMonth; day++) {
        const dayElement = document.createElement("div");
        dayElement.classList.add("calendar-day");
        dayElement.textContent = savedMoods[day] || day;
        dayElement.dataset.day = day;

        dayElement.addEventListener("click", () => selectDay(dayElement));
        calendar.appendChild(dayElement);
    }
});

// 날짜 선택
function selectDay(dayElement) {
    document.querySelectorAll(".calendar-day").forEach(el => el.classList.remove("selected"));
    dayElement.classList.add("selected");

    // 노트 작성 페이지에 선택한 날짜 반영
    const noteDateInput = document.getElementById("note-date");
    if (noteDateInput) {
        const selectedDate = new Date();
        selectedDate.setDate(dayElement.dataset.day);
        noteDateInput.value = selectedDate.toISOString().split('T')[0];
    }
}

// 감정 설정
function setMood(emoji) {
    const selectedDayElement = document.querySelector(".calendar-day.selected");
    if (!selectedDayElement) {
        alert("Please select a date first!");
        return;
    }

    const day = selectedDayElement.dataset.day;
    let savedMoods = JSON.parse(localStorage.getItem("moods")) || {};
    savedMoods[day] = emoji;
    localStorage.setItem("moods", JSON.stringify(savedMoods));

    selectedDayElement.textContent = emoji;
}
