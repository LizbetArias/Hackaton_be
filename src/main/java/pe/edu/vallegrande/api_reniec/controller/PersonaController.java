package pe.edu.vallegrande.api_reniec.controller;

import com.fasterxml.jackson.databind.JsonNode;
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

    @GetMapping("/{id}")
    public Mono<Persona> obtenerPersonaPorId(@PathVariable Long id) {
        return personaService.obtenerPersonaPorId(id);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> eliminarPersona(@PathVariable Long id) {
        return personaService.eliminarPersona(id);
    }
}
