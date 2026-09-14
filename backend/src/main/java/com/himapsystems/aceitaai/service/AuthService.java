package com.himapsystems.aceitaai.service;

import com.himapsystems.aceitaai.domain.Autonomo;
import com.himapsystems.aceitaai.dto.AuthResponse;
import com.himapsystems.aceitaai.dto.CadastroRequest;
import com.himapsystems.aceitaai.dto.LoginRequest;
import com.himapsystems.aceitaai.repository.AutonomoRepository;
import com.himapsystems.aceitaai.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AutonomoRepository autonomoRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(AutonomoRepository autonomoRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.autonomoRepository = autonomoRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse cadastrar(CadastroRequest request) {
        if (autonomoRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Ja existe uma conta com esse e-mail");
        }

        Autonomo autonomo = Autonomo.builder()
                .nomeDono(request.nomeDono())
                .nomeNegocio(request.nomeNegocio())
                .documento(request.documento())
                .whatsapp(request.whatsapp())
                .cep(request.cep())
                .logradouro(request.logradouro())
                .numero(request.numero())
                .complemento(request.complemento())
                .bairro(request.bairro())
                .cidade(request.cidade())
                .estado(request.estado())
                .email(request.email())
                .senhaHash(passwordEncoder.encode(request.senha()))
                .build();

        autonomo = autonomoRepository.save(autonomo);

        String token = jwtService.gerarToken(autonomo.getId(), autonomo.getEmail());
        return new AuthResponse(token, autonomo.getId(), autonomo.getNomeNegocio());
    }

    public AuthResponse login(LoginRequest request) {
        Autonomo autonomo = autonomoRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("E-mail ou senha invalidos"));

        if (!passwordEncoder.matches(request.senha(), autonomo.getSenhaHash())) {
            throw new IllegalArgumentException("E-mail ou senha invalidos");
        }

        String token = jwtService.gerarToken(autonomo.getId(), autonomo.getEmail());
        return new AuthResponse(token, autonomo.getId(), autonomo.getNomeNegocio());
    }
}
