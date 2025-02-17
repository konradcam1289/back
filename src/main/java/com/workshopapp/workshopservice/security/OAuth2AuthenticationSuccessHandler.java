package com.workshopapp.workshopservice.security;

import com.workshopapp.workshopservice.model.Role;
import com.workshopapp.workshopservice.model.User;
import com.workshopapp.workshopservice.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public OAuth2AuthenticationSuccessHandler(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        if (email == null || email.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Nie udało się uzyskać adresu e-mail z OAuth2.");
            return;
        }

        // Sprawdzenie, czy użytkownik istnieje w bazie
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setUsername(email);
            newUser.setEmail(email);
            newUser.setRoles(Set.of(Role.ROLE_CLIENT)); // Domyślna rola
            return userRepository.save(newUser);
        });

        // 🔹 Teraz przekazujemy **username**, a nie `OAuth2User`
        String jwt = jwtService.generateToken(user.getUsername(), user.getRoles());

        // Przekierowanie na frontend z tokenem JWT
        response.sendRedirect("http://localhost:5173/client/home?token=" + jwt);
    }
}
