fetch('/user/moods/stats')
    .then(response => response.json())
    .then(data => {
        // 메인 무드 통계
        if (data && data.mainMoodStats) {
            const mainStats = data.mainMoodStats;
            const ctx = document.getElementById("moodChart").getContext("2d");
            new Chart(ctx, {
                type: "pie",
                data: {
                    labels: ["Bad", "Poor", "Neutral", "Good", "Best"],
                    datasets: [{
                        data: [mainStats.bad, mainStats.poor, mainStats.neutral, mainStats.good, mainStats.best],
                        backgroundColor: [
                            "#ff4d4d", // bad
                            "#ffa500", // poor
                            "#d3d3d3", // neutral
                            "#90ee90", // good
                            "#32cd32"  // best
                        ]
                    }]
                },
                options: {
                    plugins: {
                        title: { display: true, text: 'Main Mood Statistics' }
                    }
                }
            });
        }
        // 서브무드 통계
        if (data && data.subMoodStats) {
            const subStats = data.subMoodStats;
            const labels = Object.keys(subStats).map(k => k.replace(":", ": "));
            const values = Object.values(subStats);
            const subMoodColorMap = {
                bad: ["#f87171", "#ef4444", "#dc2626", "#b91c1c", "#991b1b"],
                poor: ["#fca5a5", "#f87171", "#fbbf24", "#f59e42", "#f87171"],
                neutral: ["#d1d5db", "#e5e7eb", "#f3f4f6", "#9ca3af", "#6b7280"],
                good: ["#a7ffeb", "#b2f7ef", "#f6d365", "#fda085", "#ffd6e0"],
                best: ["#00e6d0", "#00cfff", "#7ee787", "#ffe066", "#ffb3ff"]
            };
            const colors = labels.map(label => {
                const [main, sub] = label.split(":");
                const arr = subMoodColorMap[main];
                if (arr && sub) {
                    // subMood 이름 인덱스 찾기
                    const subList = ["proud","grateful","energetic","excited","fulfilled","calm","productive","hopeful","motivated","friendly","indifferent","blank","tired","bored","quiet","frustrated","overwhelmed","nervous","insecure","confused","angry","sad","lonely","anxious","hopeless"];
                    let idx = -1;
                    if(main==="best") idx = ["proud","grateful","energetic","excited","fulfilled"].indexOf(sub.trim());
                    else if(main==="good") idx = ["calm","productive","hopeful","motivated","friendly"].indexOf(sub.trim());
                    else if(main==="neutral") idx = ["indifferent","blank","tired","bored","quiet"].indexOf(sub.trim());
                    else if(main==="poor") idx = ["frustrated","overwhelmed","nervous","insecure","confused"].indexOf(sub.trim());
                    else if(main==="bad") idx = ["angry","sad","lonely","anxious","hopeless"].indexOf(sub.trim());
                    if(idx>=0) return arr[idx];
                }
                // fallback
                if(main && subMoodColorMap[main]) return subMoodColorMap[main][0];
                return "#cccccc";
            });
            const ctx2 = document.getElementById("subMoodChart").getContext("2d");
            new Chart(ctx2, {
                type: "bar",
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'SubMood %',
                        data: values,
                        backgroundColor: colors
                    }]
                },
                options: {
                    indexAxis: 'y',
                    plugins: {
                        title: { display: true, text: 'SubMood Statistics' },
                        legend: { display: false }
                    },
                    scales: {
                        x: { beginAtZero: true, max: 100, title: { display: true, text: '%' } }
                    }
                }
            });
        }
    })
    .catch(error => {
        console.error("Error fetching mood statistics:", error);
    });

