package pe.edu.vallegrande.api_reniec.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@JsonIgnoreProperties(ignoreUnknown = true)  // Ignora propiedades no reconocidas
@Data
@Document("usuariosReniec")
public class Persona {
    @Id
    @JsonProperty("dni")
    private String dni;

    @JsonProperty("nombres")
    private String nombres;

    @JsonProperty("apellidoPaterno")
    private String apellidoPaterno;

    @JsonProperty("apellidoMaterno")
    private String apellidoMaterno;

    @JsonProperty("codVerifica")
    private String codVerifica;

    // Sobrescribir el método toString para una representación legible
    @Override
    public String toString() {
        return "Persona{" +
                "dni='" + dni + '\'' +
                ", nombres='" + nombres + '\'' +
                ", apellidoPaterno='" + apellidoPaterno + '\'' +
                ", apellidoMaterno='" + apellidoMaterno + '\'' +
                ", codVerifica='" + codVerifica + '\'' +
                '}';
    }


}