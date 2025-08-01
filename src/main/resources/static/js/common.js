function showAllOfferRoom(){
    let hiddenRooms = document.querySelectorAll(".Roome-container .offer-card[style='display:none;']");
    hiddenRooms.forEach(room => room.style.display = "block");
    document.getElementById("viewAllForOfferRoom").style.display = "none";//hide the btn
}
//function to check agreement of user
 function toggleSubmit(checkbox) {
   const form = checkbox.closest("form"); // Optional: scope within form
   const submitBtn = form.querySelector(".submit-btn");
   submitBtn.disabled = !checkbox.checked;
 }
// check existing username for creating new username
  function checkUsername() {
      const username = document.getElementById("username").value.trim();
      const messageSpan = document.getElementById("usernameMessage");
      const submitBtn = document.getElementById("submitBtn");

      if (username.length > 0) {
          fetch(`/users/check-username?username=${username}`)
              .then(response => response.text())
              .then(data => {
                  if (data === "exists") {
                      messageSpan.textContent = " Username is already taken. Please choose another.";
                      messageSpan.style.color = "red";
                      submitBtn.disabled = true;
                  } else {
                      messageSpan.textContent = " Username is available!";
                      messageSpan.style.color = "green";
                      submitBtn.disabled = false;
                  }
              })
              .catch(() => {
                  messageSpan.textContent = " Error checking username.";
                  messageSpan.style.color = "orange";
                  submitBtn.disabled = true;
              });
      } else {
          messageSpan.textContent = "";
          submitBtn.disabled = true;
      }
  }
  function checkEmail() {
      const email = document.getElementById("email").value;
      const messageSpan = document.getElementById("emailMessage");
      const submitBtn = document.getElementById("submitBtn");

      if (email !== "") {
          fetch(`/users/check-email?email=${email}`)
              .then(response => response.text())
              .then(result => {
                  if (result === "exists") {
                      messageSpan.textContent = " Email is already registered.";
                      messageSpan.style.color = "red";
                      submitBtn.disabled = true;
                  } else {
                      messageSpan.textContent = " Email is available.";
                      messageSpan.style.color = "green";
                      submitBtn.disabled = false;
                  }
              })
              .catch(error => {
                  messageSpan.textContent = " Error checking email.";
                  messageSpan.style.color = "orange";
                  submitBtn.disabled = true;
                  console.error("Fetch error:", error);
              });
      } else {
          messageSpan.textContent = "";
          submitBtn.disabled = true;
      }
  }



//  to scroll to search result
document.addEventListener("DOMContentLoaded", function () {
    const resultSection = document.getElementById("search-result-section");
    if (resultSection) {
      resultSection.scrollIntoView({ behavior: "smooth" });
    }
  });

  document.addEventListener("DOMContentLoaded", function() {
    var target = /*[[${scrollTo}]]*/ '';
    if (target) {
      var el = document.getElementById(target);
      if (el) {
        el.scrollIntoView({ behavior: 'smooth' });
      }
    }
  });

document.getElementById("loginBtn").addEventListener("click", function () {
    let username = document.getElementById("username").value.trim();
    let messageSpan = document.getElementById("usernameMessage");
    let alertBox = document.getElementById("alertBox");

    if (username.length > 0) {
        $.ajax({
            url: "/users/check-username",
            type: "GET",
            data: { username: username },
            success: function(response) {
                if (response === "exists") {
                    messageSpan.textContent = "Correct";
                    alertBox.classList.add("d-none");
                    alertBox.innerHTML = "";

                    console.log("Submitting form...");
                    setTimeout(() => {
                        document.getElementById("loginForm").submit();
                    }, 500);
                } else {
                    messageSpan.textContent = "Username not found!";
                    alertBox.classList.remove("d-none");
                    alertBox.innerHTML = "<strong>Error!</strong> Please enter a valid username.";

                    setTimeout(() => {
                        alertBox.classList.add("d-none");
                        messageSpan.classList.add("d-none");
                    }, 3000);
                }
            },
            error: function() {
                console.log("Error checking username");
            }
        });
    } else {
        messageSpan.textContent = "Please enter a username!";
        alertBox.classList.remove("d-none");
        alertBox.innerHTML = "<strong>Error!</strong> Please enter a username.";
        setTimeout(() => {
            alertBox.classList.add("d-none");
            messageSpan.classList.add("d-none");
        }, 3000);
    }
});
