$(document).ready(function () {
    // Register Form Submit
    $("#registerForm").submit(function (event) {
        event.preventDefault();
        const email = $("#registerEmail").val();
        const password = $("#registerPassword").val();
        const recaptchaResponse = grecaptcha.getResponse(); // Get reCAPTCHA response

        // Check if the reCAPTCHA response is empty
        if (!recaptchaResponse) {
            console.error("Please complete the reCAPTCHA.");
            $("#recaptchaError").show(); // Show the error message
            return;
        } else {
            $("#recaptchaError").hide(); // Hide the error message if reCAPTCHA is completed
        }

        // Implement registration functionality here using Axios
        const formData = new FormData();
        formData.append('email', email);
        formData.append('password', password);
        formData.append('recaptchaResponse', recaptchaResponse);

        axios.post('/api/register', formData)
            .then(response => {
                // Handle successful registration response here (if needed)
                console.log("Registration Successful");
                // Show success alert
                $("#registrationSuccessAlert").show();

                // Reset the form after successful registration
                document.getElementById("registerForm").reset();

                // Refresh the page after 2 seconds
                setTimeout(function () {
                    location.reload();
                }, 2000);
            })
            .catch(error => {
                // Handle registration error here (if needed)
                console.error("Registration Error:", error);

                // Show error message for existing email
                $("#emailError").show();

                // Refresh the page after 2 seconds
                setTimeout(function () {
                    location.reload();
                }, 2000);
            });
    });

    $(document).ready(function () {
        // Form Submit
        $("#loginForm").submit(function (event) {
            event.preventDefault();
            const email = $("#loginEmail").val();
            const password = $("#loginPassword").val();

            // Implement login functionality here using Ajax
            $.ajax({
                type: "POST",
                url: "/api/processLogin",
                data: {
                    email: email,
                    password: password
                },
                success: function (data) {
                    // Handle successful login response here
                    console.log("Login Successful");
                    // Redirect to the homepage after successful login
                    window.location.href = "/homepage.html";
                },
                error: function (xhr, status, error) {
                    // Handle login error here
                    console.error("Login Error:", error);
                    // Show error message to the user
                    $("#loginError").text("Invalid credentials");
                    $("#loginError").show();
                }
            });
        });
    });
    // Function to be called on reCAPTCHA error
    function recaptchaErrorCallback() {
        $("#recaptchaError").show(); // Show the error message
    }
    // Function to show notification message
    function showNotification(type, message) {
        const notification = $("<div class='alert mt-4'></div>");
        notification.addClass("alert-" + type);
        notification.text(message);
        $(".container").append(notification);

        // Automatically hide the notification after 3 seconds
        setTimeout(function () {
            notification.fadeOut("slow", function () {
                notification.remove();
            });
        }, 3000);
    }
});