document.addEventListener("DOMContentLoaded", function () {
    showSection("rooms");

});
function deleteReview(reviewId){
    if(confirm("Are you sure to delete this review?")){
        fetch(`/reviews/delete/${reviewId}`,{
            method: "DELETE",
        })
        .then(response => {
            if(response.ok){
                alert("review deleted successfully!");
                document.querySelector(`[data-id='${reviewId}']`).remove();
            }else {
                alert("Failed to delete the review.");
            }
        })
        .catch(error => {
              console.error("Error:", error);
              window.location.href = "/error";
        });
    }
}
function showSection(sectionId) {
    let sections = ["rooms", "dashboard", "guest","customer"];

    // Hide all sections
    sections.forEach(id => {
        let section = document.getElementById(id);
        if (section) { // Ensure section exists
            if (id === sectionId) {
                section.style.display = "block";
                section.classList.add("active");
            } else {
                section.style.display = "none";
                section.classList.remove("active");
            }
        }
    });

    // Show the selected section
    let selectedSection = document.getElementById(sectionId);
    if (selectedSection) {
        selectedSection.style.display = "block";
        setTimeout(() => {
            selectedSection.classList.add("active");
        }, 100);
    } else {
        console.error("Section not found:", sectionId);
    }
}
function checkRoomNumber() {
    let roomNumber = document.getElementById("roomNumber").value.trim();
    let roomNumberMessage = document.getElementById("roomNumberMessage");
    let stateroom = document.getElementById("saveroombtn");
    if (roomNumber.length > 0) {
        $.ajax({
            url: "/rooms/checkRoomNumber",
            type: "GET",
            data: { roomNumber: roomNumber },
            success: function(response) {
                if (response === "exists") {
                    roomNumberMessage.textContent = "That Room Number is already in use!";
                    roomNumberMessage.style.color = "red";
                    stateroom.textContent = "Not Available to save";
                    stateroom.style.color = "red";
                } else {
                    roomNumberMessage.textContent = "Room Number is available!";
                    roomNumberMessage.style.color = "green";
                    stateroom.textContent = "Add Room";
                    stateroom.style.color = "white";
                }
            },
            error: function() { console.log("Error Checking Room Number"); }
        });
    } else { roomNumberMessage.textContent = ""; }
}
function showNextFields() {
            document.getElementById("optionalFields").style.display = "block";
            document.getElementById("nextButton").style.display = "none";
}
function editShowNextFields(){
       document.getElementById("editoptionalFields").style.display = "block";
       document.getElementById("editnextButton").style.display = "none";
}
function deleteRoom(id) {
    if (confirm("Are you sure you want to delete this room?")) {
        $.ajax({
            url: `/rooms/deleteRoom/${id}`,
            type: "DELETE",
            contentType: "application/json",
            success: function(response) {
                if (response.trim() === "success") {
                    alert('Room deleted successfully!');
                    document.querySelector(`#roomRow${id}`).remove();
                    location.reload();
//                    updateRoomCount();
                } else if (response.trim() === "notFound") {
                    alert('Room Not Found!');
                } else {
                    window.location.href = "/error";
                }
            },
            error: function(error) {
                console.error('Error:', error);
                window.location.href = "/error";
            }
        });
    }
}
function showRooms(type){
    document.getElementById('allRoomsTable').style.display = type === 'all' ? '' : 'none';
        document.getElementById('availableRoomsTable').style.display = type === 'available' ? '' : 'none';
        document.getElementById('bookedRoomsTable').style.display = type === 'booked' ? '' : 'none';
}
