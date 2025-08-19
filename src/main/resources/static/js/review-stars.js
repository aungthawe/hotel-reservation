        document.addEventListener("DOMContentLoaded", () => {
          const stars = document.querySelectorAll(".star");
          const hiddenInput = document.getElementById("stars");

          stars.forEach((star, index) => {
            star.addEventListener("click", () => {
              hiddenInput.value = star.dataset.value;

              // Reset all stars
              stars.forEach(s => s.classList.remove("text-[#FFC107]"));
              // Highlight selected stars
              for (let i = 0; i <= index; i++) {
                stars[i].classList.add("text-[#FFC107]");
              }
            });
          });
        });