package com.himapsystems.aceitaai.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CpfTest {

    @Test
    void aceitaCpfValidoComOuSemMascara() {
        assertEquals("52998224725", Cpf.normalizar("529.982.247-25"));
        assertEquals("52998224725", Cpf.normalizar("52998224725"));
        assertEquals("11144477735", Cpf.normalizar(" 111.444.777-35 "));
    }

    @Test
    void rejeitaCpfInvalido() {
        assertThrows(IllegalArgumentException.class, () -> Cpf.normalizar("111.111.111-11"));
        assertThrows(IllegalArgumentException.class, () -> Cpf.normalizar("529.982.247-26"));
        assertThrows(IllegalArgumentException.class, () -> Cpf.normalizar("529.982.247-52"));
        assertThrows(IllegalArgumentException.class, () -> Cpf.normalizar("123"));
        assertThrows(IllegalArgumentException.class, () -> Cpf.normalizar("529982247250"));
        assertThrows(IllegalArgumentException.class, () -> Cpf.normalizar(""));
        assertThrows(IllegalArgumentException.class, () -> Cpf.normalizar(null));
    }

    @Test
    void comparaNumerosIgnorandoMascara() {
        assertEquals(true, Cpf.mesmoNumero("529.982.247-25", "52998224725"));
        assertEquals(true, Cpf.mesmoNumero("52998224725", "529.982.247-25"));
        assertEquals(false, Cpf.mesmoNumero("529.982.247-25", "111.444.777-35"));
        assertEquals(false, Cpf.mesmoNumero("529.982.247-25", "12.345.678/0001-90"));
        assertEquals(false, Cpf.mesmoNumero(null, "52998224725"));
    }

    @Test
    void mascaraMostraSoOsCincoUltimosDigitos() {
        assertEquals("•••.•••.247-25", Cpf.mascarar("52998224725"));
        assertEquals("", Cpf.mascarar(null));
        assertEquals("", Cpf.mascarar("123"));
    }
}
