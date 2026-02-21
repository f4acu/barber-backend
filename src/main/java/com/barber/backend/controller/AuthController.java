package com.barber.backend.controller;

import com.barber.backend.dto.AuthResponse;
import com.barber.backend.dto.LoginRequest;
import com.barber.backend.dto.RegisterRequest;
import com.barber.backend.dto.ResendEmailRequest;
import com.barber.backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok(Map.of(
            "message", "Registro exitoso. Por favor verifica tu email para activar tu cuenta."
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // NUEVO: Verificar email
    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        try {
            authService.verifyEmail(token);
            return ResponseEntity.ok(Map.of(
                "message", "¡Email verificado exitosamente! Ya puedes iniciar sesión."
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }

    // NUEVO: Reenviar email de verificación
    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestBody ResendEmailRequest request) {
        try {
            authService.resendVerificationEmail(request.getEmail());
            return ResponseEntity.ok(Map.of(
                "message", "Email de verificación reenviado. Revisa tu correo."
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }
}