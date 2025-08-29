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

//
//document.addEventListener("DOMContentLoaded", function () {
//    const resultSection = document.getElementById("search-result-section");
//    if (resultSection) {
//      resultSection.scrollIntoView({ behavior: "smooth" });
//    }
//  });

//Scroll to for every part of web page
document.addEventListener("DOMContentLoaded", function() {
  const resultSection = document.getElementById("search-result-section");
      if (resultSection) {
        resultSection.scrollIntoView({ behavior: "smooth" });
      }

    var targetId = /*[[${scrollTo}]]*/ '';
    if (!targetId) return;

    function scrollIfReady() {
      var el = document.getElementById(targetId);
      if (el) {
        el.scrollIntoView({ behavior: 'smooth', block: 'start' });
      } else {
        // retry briefly if element isn't yet present
        setTimeout(scrollIfReady, 100);
      }
    }
    scrollIfReady();

    const reviews = document.querySelectorAll(".review-card");
        reviews.forEach((card, i) => {
          setTimeout(() => {
            card.classList.remove("opacity-0", "translate-y-6");
            card.classList.add("opacity-100", "translate-y-0");
          }, i * 200); // stagger animation (200ms delay each)
        });
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

document.getElementById('download-btn').addEventListener('click', function() {
    const card = document.querySelector('img-card');

    html2canvas(card).then(function(canvas) {
      // Create a link element to download the image
      const link = document.createElement('a');
      link.href = canvas.toDataURL('image/png');
      link.download = 'reservation-confirmation.png';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    });
  });

document.addEventListener("DOMContentLoaded", () => {
      const observer = new IntersectionObserver(
        (entries) => {
          entries.forEach(entry => {
            if (entry.isIntersecting) {
              entry.target.classList.remove("opacity-0", "translate-y-6");
              entry.target.classList.add("opacity-100", "translate-y-0", "transition-all", "duration-700");
              observer.unobserve(entry.target); // animate only once
            }
          });
        },
        { threshold: 0.2 }
      );

      document.querySelectorAll(".review-card").forEach(card => {
        observer.observe(card);
      });
    });