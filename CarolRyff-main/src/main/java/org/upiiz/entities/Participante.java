package org.upiiz.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "participantes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Participante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre completo es requerido")
    @Size(min = 2, max = 200)
    @Column(name = "nombre_completo", nullable = false, length = 200)
    private String nombreCompleto;

    @NotBlank(message = "El sexo es requerido")
    @Column(name = "sexo", nullable = false, length = 20)
    private String sexo;

    @NotNull(message = "La edad es requerida")
    @Min(value = 1) @Max(value = 120)
    @Column(name = "edad", nullable = false)
    private Integer edad;

    @NotBlank(message = "El año escolar es requerido")
    @Column(name = "anio_escolar", nullable = false, length = 20)
    private String anioEscolar;

    @NotBlank(message = "El grupo es requerido")
    @Column(name = "grupo", nullable = false, length = 20)
    private String grupo;

    // NUEVO: Modalidad
    @Column(name = "modalidad", length = 30)
    private String modalidad;

    // NUEVO: Promedio semestre anterior
    @Column(name = "promedio_semestre_anterior")
    private Double promedioSemestreAnterior;

    @Column(name = "fecha_aplicacion")
    private LocalDateTime fechaAplicacion;

    @Column(name = "promedio_autoaceptacion")
    private Double promedioAutoaceptacion;

    @Column(name = "promedio_relaciones_positivas")
    private Double promedioRelacionesPositivas;

    @Column(name = "promedio_autonomia")
    private Double promedioAutonomia;

    @Column(name = "promedio_dominio_entorno")
    private Double promedioDominioEntorno;

    @Column(name = "promedio_proposito_vida")
    private Double promedioPropositoVida;

    @Column(name = "promedio_crecimiento_personal")
    private Double promedioCrecimientoPersonal;

    @Column(name = "promedio_bienestar_global")
    private Double promedioBienestarGlobal;

    @OneToMany(mappedBy = "participante", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Respuesta> respuestas;

    @PrePersist
    protected void onCreate() {
        fechaAplicacion = LocalDateTime.now();
    }
}