package com.himapsystems.aceitaai.controller;

import com.himapsystems.aceitaai.service.ComprovanteArquivo;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

final class RespostaPdf {

    private RespostaPdf() {
    }

    static ResponseEntity<byte[]> comoDownload(ComprovanteArquivo arquivo) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(arquivo.nomeArquivo()).build().toString())
                .cacheControl(CacheControl.noStore())
                .body(arquivo.conteudo());
    }
}
