package com.prison.service;

import com.prison.dto.AuthDTO;
import com.prison.model.Usuario;
import com.prison.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthDTO.LoginResponse register(AuthDTO.RegisterRequest request) {

        String senhaCriptografada = passwordEncoder.encode(request.getPassword());

        Usuario user = Usuario.builder()
                .username(request.getUsername())
                .password(senhaCriptografada)
                .nomeCompleto(request.getNomeCompleto())
                .email(request.getEmail())
                .role(Usuario.Role.ROLE_ADMIN)
                .ativo(true)
                .build();

        usuarioRepository.save(user);

        String token = jwtService.gerarToken(user.getUsername());

        return AuthDTO.LoginResponse.builder()
                .token(token)
                .tipo("Bearer")
                .username(user.getUsername())
                .role(user.getRole().name())
                .expiracaoMs(jwtService.getExpiration())
                .build();
    }

    public AuthDTO.LoginResponse login(AuthDTO.LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        Usuario user = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String token = jwtService.gerarToken(user.getUsername());

        return AuthDTO.LoginResponse.builder()
                .token(token)
                .tipo("Bearer")
                .username(user.getUsername())
                .role(user.getRole().name())
                .expiracaoMs(jwtService.getExpiration())
                .build();
    }
}