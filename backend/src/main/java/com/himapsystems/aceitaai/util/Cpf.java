package com.himapsystems.aceitaai.util;

public final class Cpf {

    private Cpf() {
    }

    public static String normalizar(String texto) {
        String digitos = texto == null ? "" : texto.replaceAll("\\D", "");
        if (!valido(digitos)) {
            throw new IllegalArgumentException("CPF inválido");
        }
        return digitos;
    }

    public static boolean mesmoNumero(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        return a.replaceAll("\\D", "").equals(b.replaceAll("\\D", ""));
    }

    public static String mascarar(String digitos) {
        if (digitos == null || digitos.length() != 11) {
            return "";
        }
        return "•••.•••." + digitos.substring(6, 9) + "-" + digitos.substring(9);
    }

    private static boolean valido(String digitos) {
        if (digitos.length() != 11 || digitos.chars().distinct().count() == 1) {
            return false;
        }
        return digitoVerificador(digitos, 9) == digitos.charAt(9) - '0'
                && digitoVerificador(digitos, 10) == digitos.charAt(10) - '0';
    }

    private static int digitoVerificador(String digitos, int quantidade) {
        int soma = 0;
        for (int i = 0; i < quantidade; i++) {
            soma += (digitos.charAt(i) - '0') * (quantidade + 1 - i);
        }
        int resto = (soma * 10) % 11;
        return resto == 10 ? 0 : resto;
    }
}
