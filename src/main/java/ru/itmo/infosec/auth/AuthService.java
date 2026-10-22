package ru.itmo.infosec.auth;

import java.util.Locale;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final UserAccountRepository users;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthService(
        UserAccountRepository users,
        PasswordEncoder passwordEncoder,
        TokenService tokenService
    ) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @Transactional
    public RegistrationResponse register(CredentialsRequest request) {
        String username = normalize(request.username());
        if (users.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Пользователь уже существует");
        }
        try {
            UserAccount saved = users.save(new UserAccount(username, passwordEncoder.encode(request.password())));
            return new RegistrationResponse(saved.getId(), saved.getUsername());
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Пользователь уже существует", exception);
        }
    }

    @Transactional(readOnly = true)
    public AuthResponse login(CredentialsRequest request) {
        String username = normalize(request.username());
        UserAccount user = users.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Неверные учетные данные"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Неверные учетные данные");
        }
        return tokenService.issue(user.getUsername());
    }

    private String normalize(String username) {
        return username.trim().toLowerCase(Locale.ROOT);
    }
}

