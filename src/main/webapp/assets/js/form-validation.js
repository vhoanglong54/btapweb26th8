(() => {
  "use strict";

  const imageTypes = ["image/jpeg", "image/png", "image/gif", "image/webp"];
  const maxImageSize = 5 * 1024 * 1024;

  const setCustomMessage = (input, message) => {
    input.setCustomValidity(message || "");
  };

  document.querySelectorAll("form[data-validate]").forEach((form) => {
    const password = form.querySelector("[data-password]");
    const confirmation = form.querySelector("[data-password-confirmation]");
    const image = form.querySelector("input[type=file][data-image-upload]");

    const validateConfirmation = () => {
      if (password && confirmation) {
        setCustomMessage(confirmation, confirmation.value && password.value !== confirmation.value ? "Xác nhận mật khẩu không khớp." : "");
      }
    };

    const validateImage = () => {
      if (!image || !image.files.length) return;
      const file = image.files[0];
      const message = !imageTypes.includes(file.type)
        ? "Chỉ chọn ảnh PNG, JPG, GIF hoặc WEBP."
        : file.size > maxImageSize
          ? "Dung lượng ảnh tối đa là 5 MB."
          : "";
      setCustomMessage(image, message);
    };

    password?.addEventListener("input", validateConfirmation);
    confirmation?.addEventListener("input", validateConfirmation);
    image?.addEventListener("change", validateImage);
    form.addEventListener("submit", (event) => {
      validateConfirmation();
      validateImage();
      if (!form.checkValidity()) {
        event.preventDefault();
        event.stopPropagation();
      }
      form.classList.add("was-validated");
    });
  });
})();
