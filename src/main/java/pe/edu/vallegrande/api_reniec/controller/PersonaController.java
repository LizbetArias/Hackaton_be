package pe.edu.vallegrande.api_reniec.controller;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.api_reniec.model.Persona;
import pe.edu.vallegrande.api_reniec.service.PersonaService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/personas")
public class PersonaController {
    private final PersonaService personaService;

    public PersonaController(PersonaService personaService) {
        this.personaService = personaService;
    }

    @GetMapping("/consultar/{dni}")
    public Mono<JsonNode> consultarDni(@PathVariable String dni) {
        return personaService.consultarDni(dni);  // Esto ahora devuelve un Mono<JsonNode>
    }

    @PostMapping("/registrar/{dni}")
    public Mono<Persona> registrarPersona(@PathVariable String dni) {
        return personaService.registrarPersona(dni);
    }

    @GetMapping("/list")
    public Flux<Persona> listarPersonas() {
        return personaService.listarPersonas();
    }

    @GetMapping("/{dni}")
    public Mono<Persona> obtenerPersonaPorDni(@PathVariable String dni) {
        return personaService.obtenerPersonaPorDni(dni);  // Ahora obtenemos por DNI
    }

    // Endpoint para eliminar una persona lógicamente (cambiar status a 'I') por DNI
    @PutMapping("/eliminar/{dni}")
    public Mono<Void> eliminarPersona(@PathVariable String dni) {
        return personaService.eliminarPersonaPorDni(dni);  // Eliminar lógica por DNI
    }

    // Endpoint para restaurar una persona (cambiar status a 'A') por DNI
    @PutMapping("/restaurar/{dni}")
    public Mono<Persona> restaurarPersona(@PathVariable String dni) {
        return personaService.restaurarPersonaPorDni(dni);  // Restaurar por DNI
    }

    // Endpoint para eliminar físicamente una persona por DNI
    @DeleteMapping("/eliminar-fisicamente/{dni}")
    public Mono<Void> eliminarPersonaFisicamente(@PathVariable String dni) {
        return personaService.eliminarPersonaFisicamentePorDni(dni);  // Eliminar físicamente por DNI
    }

    // Endpoint para actualizar una persona por DNI
    @PutMapping("/actualizar/{dniActual}/{nuevoDni}")
    public Mono<ResponseEntity<Persona>> actualizarPersona(
            @PathVariable("dniActual") String dniActual,
            @PathVariable("nuevoDni") String nuevoDni) {
        
        return personaService.actualizarPersonaPorDni(dniActual, nuevoDni)
                .map(persona -> ResponseEntity.ok(persona))  // Si se encuentra y actualiza, devuelve la persona
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));  // Si no se encuentra, devuelve un 404
    }
    

}
