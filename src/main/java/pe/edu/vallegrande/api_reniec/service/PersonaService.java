package pe.edu.vallegrande.api_reniec.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.vallegrande.api_reniec.model.Persona;
import pe.edu.vallegrande.api_reniec.repository.PersonaRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PersonaService {
    private final PersonaRepository personaRepository;
    private final WebClient webClient;
    private static final String TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6Imp1bGlvLnF1aXNwZUB2YWxsZWdyYW5kZS5lZHUucGUifQ.6M-P2QMMvKFZEeMvTUXvkOooM02N_pWqt0OdlaYW3PM";

    public PersonaService(PersonaRepository personaRepository, WebClient.Builder webClientBuilder) {
        this.personaRepository = personaRepository;
        this.webClient = webClientBuilder.baseUrl("https://dniruc.apisperu.com/api/v1").build();
    }

    public Mono<JsonNode> consultarDni(String dni) {
        System.out.println("Iniciando consulta de DNI: " + dni);

        String uri = "/dni/" + dni + "?token=" + TOKEN;
        System.out.println("URI generada: " + uri);

        return webClient.get()
                .uri(uri)
                .header("Accept", "application/json")  // Asegura que el servidor devuelva JSON
                .retrieve()
                .bodyToMono(JsonNode.class)  // Recibe la respuesta directamente como un JsonNode
                .doOnNext(response -> System.out.println("Respuesta JSON: " + response.toString())) // Imprime el JSON recibido
                .doOnError(error -> System.err.println("Error en la consulta: " + error.getMessage()));
    }

    public Mono<Persona> registrarPersona(String dni) {
        return consultarDni(dni)  // Obtienes la respuesta como JsonNode
                .flatMap(jsonNode -> {
                    try {
                        // Imprimir el contenido del JsonNode para asegurarse de que es correcto
                        System.out.println("Respuesta JSON recibida: " + jsonNode.toString());

                        // Convertir el JsonNode en un objeto Persona
                        ObjectMapper objectMapper = new ObjectMapper();
                        Persona persona = objectMapper.treeToValue(jsonNode, Persona.class);

                        // Establecer explícitamente el status como 'A'
                        persona.setStatus("A");

                        // Imprimir el objeto Persona para verificar que la conversión fue correcta
                        System.out.println("Objeto Persona creado con status 'A': " + persona);

                        // Guardar el objeto Persona en la base de datos
                        return personaRepository.save(persona)
                                .doOnSuccess(savedPersona -> System.out.println("Persona guardada correctamente: " + savedPersona));
                    } catch (Exception e) {
                        // Imprimir detalles del error para ayudar a depurar
                        System.err.println("Error al convertir JSON a Persona: " + e.getMessage());
                        e.printStackTrace();
                        return Mono.error(new RuntimeException("Error al convertir JSON a Persona", e));
                    }
                });
    }

    public Flux<Persona> listarPersonas() {
        return personaRepository.findAll();
    }

    public Mono<Persona> obtenerPersonaPorDni(String dni) {
        return personaRepository.findByDni(dni);  // Método para obtener persona por DNI
    }

    // Método de eliminación lógica por DNI
    public Mono<Void> eliminarPersonaPorDni(String dni) {
        return personaRepository.findByDni(dni)
                .flatMap(persona -> {
                    // Cambiar el status a 'I' para eliminar lógicamente
                    persona.setStatus("I");
                    return personaRepository.save(persona).then();
                });
    }

    // Método de eliminación física por DNI (eliminar permanentemente)
    public Mono<Void> eliminarPersonaFisicamentePorDni(String dni) {
        return personaRepository.findByDni(dni)
                .flatMap(persona -> {
                    // Eliminar la persona de la base de datos de manera permanente
                    return personaRepository.delete(persona).then();
                });
    }

    // Método de restauración por DNI
    public Mono<Persona> restaurarPersonaPorDni(String dni) {
        return personaRepository.findByDni(dni)
                .flatMap(persona -> {
                    // Cambiar el status a 'A' para restaurar lógicamente
                    persona.setStatus("A");
                    return personaRepository.save(persona);
                });
    }

    // Método para actualizar una persona por DNI
    public Mono<Persona> actualizarPersonaPorDni(String dniActual, String nuevoDni) {
        if (dniActual.equals(nuevoDni)) {
            return Mono.error(new RuntimeException("El nuevo DNI no puede ser igual al actual."));
        }
    
        // Buscar la persona por el DNI actual
        return personaRepository.findByDni(dniActual)
                .flatMap(personaExistente -> 
                    // Consulta la nueva información de RENIEC para obtener los nuevos datos
                    consultarDni(nuevoDni)  
                        .flatMap(jsonNode -> {
                            try {
                                // Aquí actualizamos solo los campos que no sean el DNI
                                personaExistente.setNombres(jsonNode.get("nombres").asText());
                                personaExistente.setApellidoPaterno(jsonNode.get("apellidoPaterno").asText());
                                personaExistente.setApellidoMaterno(jsonNode.get("apellidoMaterno").asText());
                                personaExistente.setCodVerifica(jsonNode.get("codVerifica").asText());
    
                                // Guardar la persona actualizada en la base de datos
                                return personaRepository.save(personaExistente);
                            } catch (Exception e) {
                                return Mono.error(new RuntimeException("Error al procesar los datos de RENIEC", e));
                            }
                        })
                )
                .switchIfEmpty(Mono.error(new RuntimeException("No se encontró una persona con el DNI actual: " + dniActual)));
    }
    
    
}
