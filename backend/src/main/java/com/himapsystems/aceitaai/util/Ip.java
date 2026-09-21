package com.himapsystems.aceitaai.util;

public final class Ip {

    private Ip() {
    }

    /**
     * Oculta a parte final do endereco para documentos que circulam fora do sistema (ex: 191.34.xx.xx).
     * O endereco completo continua guardado no aceite.
     */
    public static String mascarar(String ip) {
        if (ip == null || ip.isBlank()) {
            return "não informado";
        }
        String valor = ip.trim();

        if (valor.contains(":")) {
            String[] grupos = valor.split(":");
            String primeiro = grupos.length > 0 && !grupos[0].isEmpty() ? grupos[0] : "0";
            String segundo = grupos.length > 1 && !grupos[1].isEmpty() ? grupos[1] : "0";
            return primeiro + ":" + segundo + ":xxxx:xxxx";
        }

        String[] octetos = valor.split("\\.");
        if (octetos.length == 4) {
            return octetos[0] + "." + octetos[1] + ".xx.xx";
        }
        return "não informado";
    }
}
