package com.workshopapp.workshopservice.controller;

import com.workshopapp.workshopservice.dto.LoginRequest;
import com.workshopapp.workshopservice.dto.RegisterRequest;
import com.workshopapp.workshopservice.model.Role;
import com.workshopapp.workshopservice.model.User;
import com.workshopapp.workshopservice.repository.UserRepository;
import com.workshopapp.workshopservice.security.JwtService;
import com.workshopapp.workshopservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(UserService userService, UserRepository userRepository, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        User user = userService.registerUser(request);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 🔹 Pobieramy username oraz role użytkownika
        String username = user.getUsername();
        Set<Role> roles = user.getRoles();

        // 🔹 Generujemy token JWT
        String jwt = jwtService.generateToken(username, roles);

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("username", username);
        response.put("role", user.getPrimaryRole());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // 🔹 Generujemy token dla użytkownika
            String jwt = jwtService.generateToken(user.getUsername(), user.getRoles());

            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("username", user.getUsername());
            response.put("role", user.getPrimaryRole());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Błędne dane logowania");
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("Brak autoryzacji");
        }

        String username = authentication.getName();
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isPresent()) {
            return ResponseEntity.ok(Map.of(
                    "username", user.get().getUsername(),
                    "email", user.get().getEmail(),
                    "firstName", user.get().getFirstName(),
                    "lastName", user.get().getLastName(),
                    "phoneNumber", user.get().getPhoneNumber(),
                    "address", user.get().getAddress(),
                    "role", user.get().getPrimaryRole()
            ));
        } else {
            return ResponseEntity.status(404).body("Użytkownik nie znaleziony");
        }
    }

    @GetMapping("/oauth2/success")
    public ResponseEntity<?> oauth2LoginSuccess(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof OAuth2User)) {
            return ResponseEntity.status(401).body("Błąd autoryzacji OAuth2");
        }

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(400).body("Nie udało się uzyskać emaila z OAuth2");
        }

        // 🔹 Sprawdzamy, czy użytkownik już istnieje w bazie
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setUsername(email);
                    newUser.setEmail(email);
                    newUser.setRoles(Set.of(Role.ROLE_CLIENT));
                    return userRepository.save(newUser);
                });

        // 🔹 Tworzymy token dla użytkownika
        String jwt = jwtService.generateToken(user.getUsername(), user.getRoles());

        return ResponseEntity.ok(Map.of(
                "token", jwt,
                "username", user.getUsername(),
                "role", user.getPrimaryRole()
        ));
    }
}
