package com.himapsystems.aceitaai.repository;

import com.himapsystems.aceitaai.domain.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Page<Cliente> findByAutonomoId(Long autonomoId, Pageable pageable);

    Page<Cliente> findByAutonomoIdAndNomeContainingIgnoreCase(Long autonomoId, String nome, Pageable pageable);

    Optional<Cliente> findByIdAndAutonomoId(Long id, Long autonomoId);
}
