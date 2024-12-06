package pe.edu.vallegrande.api_reniec.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import pe.edu.vallegrande.api_reniec.model.Persona;

public interface PersonaRepository extends ReactiveCrudRepository<Persona, Long> {
}
