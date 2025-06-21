package com.workshopapp.workshopservice.controller;

import com.workshopapp.workshopservice.dto.UpdateClientRequest;
import com.workshopapp.workshopservice.dto.ClientProfileResponse;
import com.workshopapp.workshopservice.model.User;
import com.workshopapp.workshopservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client")
@PreAuthorize("hasAuthority('ROLE_CLIENT')")
@CrossOrigin(origins = "http://localhost:5173")
public class ClientController {

    private final UserService userService;

    public ClientController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<ClientProfileResponse> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        ClientProfileResponse response = new ClientProfileResponse(
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getAddress()
        );
        return ResponseEntity.ok(response);
    }


    @PutMapping("/profile")
    public ResponseEntity<ClientProfileResponse> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UpdateClientRequest request) {

        User updated = userService.updateClientProfile(userDetails.getUsername(), request);

        ClientProfileResponse response = new ClientProfileResponse(
                updated.getUsername(),
                updated.getEmail(),
                updated.getFirstName(),
                updated.getLastName(),
                updated.getPhoneNumber(),
                updated.getAddress()
        );
        return ResponseEntity.ok(response);
    }
}
