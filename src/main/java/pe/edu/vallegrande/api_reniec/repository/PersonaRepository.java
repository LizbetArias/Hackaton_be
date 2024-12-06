package pe.edu.vallegrande.api_reniec.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.api_reniec.model.Persona;
import reactor.core.publisher.Mono;

@Repository
public interface PersonaRepository extends ReactiveMongoRepository<Persona, String> {
    Mono<Persona> findByDni(String dni);
}