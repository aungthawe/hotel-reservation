// static/js/register.js
document.addEventListener("DOMContentLoaded", function() {
    function checkUsername() {
        let username = document.getElementById("username").value.trim();
        let messageSpan = document.getElementById("usernameMessage");
        if (username.length > 0) {
            $.ajax({
                url: "/users/check-username",
                type: "GET",
                data: { username: username },
                success: function(response) {
                    console.log(response);  // Log response to see what it contains
                    if (response === "exists") {
                        messageSpan.textContent = "Username already exists!";
                        messageSpan.style.color = "red";
                    } else {
                        messageSpan.textContent = "Username is available!";
                        messageSpan.style.color = "green";
                    }
                },
                error: function(xhr, status, error) {
                    console.log("Error checking username: ", error);
                }

            });
        } else {
            messageSpan.textContent = "";
        }
    }

    function checkEmail() {
        let email = document.getElementById("email").value.trim();
        let emailMessage = document.getElementById("emailMessage");

        if (email.length > 5) {
            $.ajax({
                url: "/users/check-email",
                type: "GET",
                data: { email: email },
                success: function(response) {
                   if (response === "exists") {
                    emailMessage.textContent = "Email already exists!";
                    emailMessage.style.color = "red";  // Set text color to red
                   } else {
                     emailMessage.textContent = "";  // Clear the message
                   }
                },
                error: function() {
                    emailMessage.textContent = "Error checking Email!";
                    emailMessage.style.color = "red";
                }
            });
        } else {
            emailMessage.textContent = "Email is required!";
            emailMessage.style.color = "red";
        }
    }

    // Attach the checkUsername function to the input field on keyup event
    document.getElementById("username").addEventListener("keyup", checkUsername);
    document.getElementById("email").addEventListener("keyup",checkEmail);
});


function showStep2() {
    const step1 = document.getElementById('step1');
    const step2 = document.getElementById('step2');

    step1.classList.add('fade-out');
    setTimeout(() => {
        step1.style.display = 'none';
        step2.style.display = 'block';
        step2.classList.add('fade-in');
    }, 500);
}

function showStep1(){
    const step1 = document.getElementById('step1');
    const step2 = document.getElementById('step2');

    step1.classList.add('fade-out');
    setTimeout(() => {
            step1.style.display = 'block';
            step2.style.display = 'none';
            step1.classList.add('fade-in');
    }, 500);
}
