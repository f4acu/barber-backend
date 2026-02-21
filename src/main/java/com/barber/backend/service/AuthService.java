package com.barber.backend.service;

import com.barber.backend.config.JwtUtil;
import com.barber.backend.dto.AuthResponse;
import com.barber.backend.dto.LoginRequest;
import com.barber.backend.dto.RegisterRequest;
import com.barber.backend.model.Role;
import com.barber.backend.model.User;
import com.barber.backend.model.VerificationToken;
import com.barber.backend.repository.UserRepository;
import com.barber.backend.repository.VerificationTokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService; // NUEVO
    private final VerificationTokenRepository tokenRepository; // NUEVO

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       EmailService emailService,
                       VerificationTokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
        this.tokenRepository = tokenRepository;
    }

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Crear usuario
        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.CLIENT);
        user.setEmailVerified(false); // NUEVO: Por defecto no verificado

        User savedUser = userRepository.save(user);

        // NUEVO: Generar token de verificación
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(24); // Expira en 24 horas

        VerificationToken verificationToken = new VerificationToken(token, savedUser, expiryDate);
        tokenRepository.save(verificationToken);

        // NUEVO: Enviar email de verificación
        try {
            emailService.sendVerificationEmail(
                savedUser.getEmail(),
                savedUser.getName(),
                token
            );
        } catch (Exception e) {
            // Log del error pero no fallar el registro
            System.err.println("Error al enviar email de verificación: " + e.getMessage());
        }
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        // NUEVO: Validar que el email esté verificado
        if (!user.isEmailVerified()) {
            throw new RuntimeException("Debes verificar tu email antes de iniciar sesión. Revisa tu correo.");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getRole().name());
    }

    // NUEVO: Verificar email
    @Transactional
    public void verifyEmail(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token de verificación inválido"));

        if (verificationToken.isUsed()) {
            throw new RuntimeException("Este token ya fue utilizado");
        }

        if (verificationToken.isExpired()) {
            throw new RuntimeException("El token de verificación ha expirado");
        }

        // Marcar usuario como verificado
        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        // Marcar token como usado
        verificationToken.setUsed(true);
        tokenRepository.save(verificationToken);
    }

    // NUEVO: Reenviar email de verificación
    @Transactional
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (user.isEmailVerified()) {
            throw new RuntimeException("Tu email ya está verificado");
        }

        // Eliminar tokens anteriores
        tokenRepository.deleteByUserId(user.getId());

        // Generar nuevo token
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(24);

        VerificationToken verificationToken = new VerificationToken(token, user, expiryDate);
        tokenRepository.save(verificationToken);

        // Enviar email
        emailService.sendVerificationEmail(
            user.getEmail(),
            user.getName(),
            token
        );
    }
}