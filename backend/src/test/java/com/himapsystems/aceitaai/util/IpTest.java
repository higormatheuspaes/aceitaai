package com.himapsystems.aceitaai.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IpTest {

    @Test
    void ocultaOsDoisUltimosOctetosDoIpv4() {
        assertEquals("191.34.xx.xx", Ip.mascarar("191.34.12.200"));
    }

    @Test
    void ocultaOFinalDoIpv6() {
        assertEquals("2804:14d:xxxx:xxxx", Ip.mascarar("2804:14d:8e81:1234:0:0:0:1"));
    }

    @Test
    void loopbackIpv6NaoQuebra() {
        assertEquals("0:0:xxxx:xxxx", Ip.mascarar("::1"));
    }

    @Test
    void valorAusenteOuInvalidoViraNaoInformado() {
        assertEquals("não informado", Ip.mascarar(null));
        assertEquals("não informado", Ip.mascarar(" "));
        assertEquals("não informado", Ip.mascarar("desconhecido"));
    }
}
