var ctx = document.getElementById('occupancyChart').getContext('2d');
var occupancyChart = new Chart(ctx, {
    type: 'bar',
    data: {
        labels: ['Mercury', 'Venus', 'Earth', 'Mars', 'Jupiter', 'Saturn', 'Uranus', 'Neptune'],
        datasets: [{
            label: 'Occupancy',
            data: [10, 25, 30, 10, 50, 25, 24, 30],
            backgroundColor: 'rgba(0, 180, 216, 0.6)'
        }]
    },
    options: {
        responsive: true,
        scales: {
            y: {
                beginAtZero: true
            }
        }
    }
});