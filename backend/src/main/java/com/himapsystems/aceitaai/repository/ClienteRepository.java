package com.himapsystems.aceitaai.repository;

import com.himapsystems.aceitaai.domain.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    List<Cliente> findByAutonomoIdOrderByNomeAsc(Long autonomoId);

    Optional<Cliente> findByIdAndAutonomoId(Long id, Long autonomoId);
}
