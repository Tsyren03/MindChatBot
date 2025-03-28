document.addEventListener("DOMContentLoaded", function () {
    const calendar = document.getElementById("calendar");
    const currentDateElement = document.getElementById("current-date");
    const prevMonthButton = document.getElementById("prev-month");
    const nextMonthButton = document.getElementById("next-month");
    const currentMonthElement = document.getElementById("current-month");

    let currentYear = new Date().getFullYear();
    let currentMonth = new Date().getMonth();

    async function fetchMoods(year, month) {
        const response = await fetch(`/api/moods/${year}/${month + 1}`);
        return response.json();
    }

    async function updateCalendar() {
        calendar.innerHTML = "";
        currentMonthElement.textContent = new Date(currentYear, currentMonth).toLocaleString('default', { month: 'long', year: 'numeric' });

        const daysInMonth = new Date(currentYear, currentMonth + 1, 0).getDate();
        const savedMoods = await fetchMoods(currentYear, currentMonth);

        for (let day = 1; day <= daysInMonth; day++) {
            const dayElement = document.createElement("div");
            dayElement.classList.add("calendar-day");

            // Find the mood for the current day and change the color
            const mood = savedMoods.find(m => m.day === day);
            dayElement.textContent = day;
            dayElement.dataset.day = day;

            // Apply color based on the mood value using the updated color scheme
            if (mood) {
                switch (mood.emoji) {
                    case 'bad':
                        dayElement.style.backgroundColor = '#ff4d4d'; // 빨강
                        break;
                    case 'poor':
                        dayElement.style.backgroundColor = '#ffa500'; // 주황
                        break;
                    case 'neutral':
                        dayElement.style.backgroundColor = '#d3d3d3'; // 회색
                        break;
                    case 'good':
                        dayElement.style.backgroundColor = '#90ee90'; // 연두색
                        break;
                    case 'best':
                        dayElement.style.backgroundColor = '#32cd32'; // 초록
                        break;
                    default:
                        dayElement.style.backgroundColor = 'transparent';
                        break;
                }
            }

            dayElement.addEventListener("click", () => selectDay(dayElement));
            calendar.appendChild(dayElement);
        }
    }

    // Event listeners for navigating between months
    prevMonthButton.addEventListener("click", () => {
        currentMonth--;
        if (currentMonth < 0) {
            currentMonth = 11;
            currentYear--;
        }
        updateCalendar();
    });

    nextMonthButton.addEventListener("click", () => {
        currentMonth++;
        if (currentMonth > 11) {
            currentMonth = 0;
            currentYear++;
        }
        updateCalendar();
    });

    const today = new Date();
    currentDateElement.textContent = `Today: ${today.toDateString()}`;

    updateCalendar();  // Initial call to load the calendar
});

// Function to select a day
function selectDay(dayElement) {
    document.querySelectorAll(".calendar-day").forEach(el => el.classList.remove("selected"));
    dayElement.classList.add("selected");

    const noteDateInput = document.getElementById("note-date");
    if (noteDateInput) {
        const selectedDate = new Date();
        selectedDate.setDate(dayElement.dataset.day);
        noteDateInput.value = selectedDate.toISOString().split('T')[0];
    }
}

// Function to set mood for the selected day
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
        emoji: moodValue  // Use mood values like 'bad', 'poor', etc.
    };

    await fetch('/api/moods', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(mood)
    });

    // Update the cell's background color based on the selected mood
    switch (moodValue) {
        case 'bad':
            selectedDayElement.style.backgroundColor = '#ff4d4d'; // 빨강
            break;
        case 'poor':
            selectedDayElement.style.backgroundColor = '#ffa500'; // 주황
            break;
        case 'neutral':
            selectedDayElement.style.backgroundColor = '#d3d3d3'; // 회색
            break;
        case 'good':
            selectedDayElement.style.backgroundColor = '#90ee90'; // 연두색
            break;
        case 'best':
            selectedDayElement.style.backgroundColor = '#32cd32'; // 초록
            break;
    }
}
