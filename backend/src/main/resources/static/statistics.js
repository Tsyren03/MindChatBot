fetch('/user/moods/stats')
    .then(response => response.json())
    .then(data => {
        if (data && data.bad !== undefined && data.poor !== undefined && data.neutral !== undefined && data.good !== undefined && data.best !== undefined) {
            const ctx = document.getElementById("moodChart").getContext("2d");
            new Chart(ctx, {
                type: "pie",
                data: {
                    labels: ["Bad", "Poor", "Neutral", "Good", "Best"],
                    datasets: [{
                        data: [data.bad, data.poor, data.neutral, data.good, data.best],
                        backgroundColor: ["#ff4d4d", "#ffa500", "#d3d3d3", "#90ee90", "#32cd32"]
                    }]
                }
            });
        } else {
            console.error("Invalid data received for mood statistics:", data);
        }
    })
    .catch(error => {
        console.error("Error fetching mood statistics:", error);
    });