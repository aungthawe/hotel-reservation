
  document.addEventListener("DOMContentLoaded", () => {
    const alerts = document.querySelectorAll("#floating-alerts .floating-alert");
    alerts.forEach((el, idx) => {
      // base display duration
      const displayMs = 3500;
      const extraStagger = idx * 300; // if multiple, stagger
      setTimeout(() => startDismiss(el), displayMs + extraStagger);
    });
  });

  function startDismiss(el) {
    if (!el) return;
    el.classList.add("exit");
    el.addEventListener("animationend", () => {
      el.remove();
    });
  }

  function dismissFloatingAlert(btn) {
    const alertEl = btn.closest(".floating-alert");
    startDismiss(alertEl);
  }
