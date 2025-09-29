$(document).ready(function() {
	// Tab switching
	$("#login-tab").click(function() {
		$("#login-tab").addClass("active");
		$("#signup-tab").removeClass("active");
		$("#login-form").addClass("active");
		$("#signup-form").removeClass("active");
	});

	$("#signup-tab").click(function() {
		$("#signup-tab").addClass("active");
		$("#login-tab").removeClass("active");
		$("#signup-form").addClass("active");
		$("#login-form").removeClass("active");
	});

	// Enhanced Toast Notification
	function showToast(message, type = "success") {
		const toast = document.createElement("div");
		toast.className = `toast ${type}`;
		toast.innerHTML = message;
		document.body.appendChild(toast);

		setTimeout(() => {
			toast.classList.add("show");
		}, 10);

		setTimeout(() => {
			toast.classList.remove("show");
			setTimeout(() => {
				toast.remove();
			}, 300);
		}, 3000);
	}

	// Validation functions
	function validateEmail(email) {
		const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
		return re.test(email);
	}

	function validatePassword(password) {
		return password.length >= 8;
	}

	function validateName(name) {
		return name.trim().length >= 2;
	}

	// Handle Signup Form Submission with AJAX
	$("#signup-form-element").submit(function(event) {
		event.preventDefault();

		let isValid = true;

		// Clear previous errors
		["fullname", "signup-email", "signup-password", "confirm-password"].forEach(field => {
			$(`#${field}`).removeClass("error");
			$(`#${field}-error`).hide();
		});

		let fullname = $("#fullname").val();
		let email = $("#signup-email").val();
		let password = $("#signup-password").val();
		let confirmPassword = $("#confirm-password").val();

		if (!validateName(fullname)) {
			$("#fullname").addClass("error");
			showToast(" Name must be at least 2 characters long!", "error");
			isValid = false;
		}

		if (!validateEmail(email)) {
			$("#signup-email").addClass("error");
			showToast(" Invalid email format!", "error");
			isValid = false;
		}

		if (!validatePassword(password)) {
			$("#signup-password").addClass("error");
			showToast("Password must be at least 8 characters long!", "error");
			isValid = false;
		}

		if (password !== confirmPassword) {
			$("#confirm-password").addClass("error");
			showToast(" Passwords do not match!", "error");
			isValid = false;
		}

		if (isValid) {
			let formData = {
				fullName: fullname,
				email: email,
				password: password
			};

			$.ajax({
				url: "/signup",
				type: "POST",
				contentType: "application/json",
				data: JSON.stringify(formData),
				success: function() {
					showToast(" Account created successfully!", "success");
					setTimeout(() => {
						location.reload();
					}, 2000);
				},
				error: function(xhr) {
					let errorMessage = " Something went wrong!";
					if (xhr.status === 400) {
						errorMessage = xhr.responseText; // Show backend error message
					}
					showToast(errorMessage, "error");
				}
			});
		}
	});

	// Handle Login Form Submission (if needed)
	$("#login-form-element").submit(function(event) {
	event.preventDefault();

	let email = $("#email").val();
	let password = $("#password").val();
	let isValid = true;

	// Clear previous errors
	["email", "password"].forEach(field => {
		$(`#${field}`).removeClass("error");
		$(`#${field}-error`).hide();
	});

	if (!validateEmail(email)) {
		$("#email").addClass("error");
		showToast("Invalid email format!", "error");
		isValid = false;
	}

	if (!validatePassword(password)) {
		$("#password").addClass("error");
		showToast("Password must be at least 8 characters long!", "error");
		isValid = false;
	}

	if (isValid) {
		$.ajax({
			type: "POST",
			url: "/login",
			data: {
				email: email,
				password: password
			},
			success: function(response) {
				
				if (response === "success") {
					showToast("Login successful! Redirecting...", "success");
					setTimeout(() => {
						window.location.href = "/index";
					}, 2000);
				} else {
					showToast("Invalid email or password!", "error");
				}
			},
			error: function() {
				
				showToast("Server error! Please try again later.", "error");
			}
		});
	}
});

});
