package org.example.controller;

import org.example.model.Login;
import org.example.model.LoginDto;
import org.example.repository.LoginRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

@Controller
@RequestMapping("/api")
public class LoginController {

    private final LoginRepository loginRepository;
    private final String RECAPTCHA_SECRET_KEY = "6LdH84UnAAAAAKQ-BWQM9wWZmE4Nv7o8FHQvyKoq"; // Replace with your actual reCAPTCHA secret key

    private static final Logger logger = Logger.getLogger(LoginController.class.getName());


    @Autowired
    public LoginController(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("login", new LoginDto());
        return "login";
    }

    @PostMapping("/processLogin")
    public String processLogin(@ModelAttribute LoginDto loginDto, Model model) {
        // Pobierz użytkownika z bazy danych na podstawie adresu email
        Login user = loginRepository.findByEmail(loginDto.getEmail());
        if (user != null && BCrypt.checkpw(loginDto.getPassword(), user.getPassword())) {
            // Jeśli dane są poprawne, przekieruj na stronę powitalną (homepage.html)
            return "redirect:/homepage.html";
        } else {
            // Jeśli dane są niepoprawne, wyświetl odpowiedni komunikat na stronie logowania
            model.addAttribute("loginError", "Invalid E-mail or Password");
            model.addAttribute("login", new LoginDto());
            return "login";
        }
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("login", new Login());
        return "register";
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestParam String email, @RequestParam String password, @RequestParam String recaptchaResponse) {
        // Validate reCAPTCHA response
        boolean recaptchaVerified = verifyRecaptcha(recaptchaResponse);
        if (!recaptchaVerified) {
            return ResponseEntity.badRequest().body("Failed to verify reCAPTCHA.");
        }

        // Check if the email is already registered
        Login existingUser = loginRepository.findByEmail(email);
        if (existingUser != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("An account with this email already exists.");
        }

        // Hash the password using BCrypt
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        // Create a new Login object
        Login login = new Login();
        login.setEmail(email);
        login.setPassword(hashedPassword);

        // Przypisanie własnego identyfikatora dla obiektu Login
        login.setId(UUID.randomUUID().toString());

        // Save the login data to the database
        loginRepository.save(login);

        // Return a response with a success message
        return ResponseEntity.status(HttpStatus.CREATED).body("Registration successful!");
    }

    private boolean verifyRecaptcha(String recaptchaResponse) {
        final String RECAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify";

        // Prepare the request body to be sent to Google reCAPTCHA API
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("secret", RECAPTCHA_SECRET_KEY);
        requestBody.add("response", recaptchaResponse);

        // Create a RestTemplate to make the API call
        RestTemplate restTemplate = new RestTemplate();

        // Send a POST request to the reCAPTCHA API and get the response
        ResponseEntity<Map> apiResponse = restTemplate.postForEntity(RECAPTCHA_URL, requestBody, Map.class);
        if (apiResponse.getBody() != null) {
            // Check if the reCAPTCHA verification was successful
            boolean success = (Boolean) apiResponse.getBody().get("success");
            return success;
        }
        return false;
    }

    // Obsługa żądania POST dla niepoprawnych danych logowania
    @PostMapping("/login")
    public String processLoginPost(@ModelAttribute LoginDto loginDto, Model model) {
        // Wyświetl odpowiedni komunikat na stronie logowania
        model.addAttribute("loginError", "Invalid credentials");
        return "login";
    }
}
