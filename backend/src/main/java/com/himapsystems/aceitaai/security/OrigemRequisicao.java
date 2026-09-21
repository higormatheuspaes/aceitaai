package com.himapsystems.aceitaai.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Descobre IP e user-agent de quem esta respondendo o orcamento. Como as chamadas passam pelo servidor do
 * Next, o IP real so e aceito quando a requisicao traz a chave interna compartilhada entre os dois servidores;
 * sem ela, qualquer um poderia forjar o IP chamando a API direto.
 */
@Component
public class OrigemRequisicao {

    public record Origem(String ip, String userAgent) {}

    private static final String IP_DESCONHECIDO = "desconhecido";

    private final byte[] chaveInterna;

    public OrigemRequisicao(@Value("${app.internal-key:}") String chaveInterna) {
        this.chaveInterna = chaveInterna.getBytes(StandardCharsets.UTF_8);
    }

    public Origem resolver(HttpServletRequest request) {
        if (!requisicaoConfiavel(request)) {
            return new Origem(request.getRemoteAddr(), limitar(request.getHeader("User-Agent"), 255));
        }

        String ip = limitar(request.getHeader("X-Client-Ip"), 45);
        if (ip == null || !ip.matches("[0-9a-fA-F:.]{2,45}")) {
            ip = IP_DESCONHECIDO;
        }
        return new Origem(ip, limitar(request.getHeader("X-Client-User-Agent"), 255));
    }

    private boolean requisicaoConfiavel(HttpServletRequest request) {
        String recebida = request.getHeader("X-Internal-Key");
        return chaveInterna.length > 0
                && recebida != null
                && MessageDigest.isEqual(chaveInterna, recebida.getBytes(StandardCharsets.UTF_8));
    }

    private String limitar(String valor, int maximo) {
        if (valor == null) {
            return null;
        }
        String limpo = valor.trim();
        return limpo.length() > maximo ? limpo.substring(0, maximo) : limpo;
    }
}
