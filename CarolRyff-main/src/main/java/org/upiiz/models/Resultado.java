package org.upiiz.models;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resultado {

    private Long participanteId;
    private String nombreCompleto;
    private String sexo;
    private Integer edad;
    private String anioEscolar;
    private String grupo;

    // NUEVO
    private String modalidad;
    private Double promedioSemestreAnterior;

    private Double autoaceptacion;
    private Double relacionesPositivas;
    private Double autonomia;
    private Double dominioEntorno;
    private Double propositoVida;
    private Double crecimientoPersonal;
    private Double bienestarGlobal;

    public String getNivelAutoaceptacion()      { return nivel(autoaceptacion); }
    public String getNivelRelacionesPositivas() { return nivel(relacionesPositivas); }
    public String getNivelAutonomia()           { return nivel(autonomia); }
    public String getNivelDominioEntorno()      { return nivel(dominioEntorno); }
    public String getNivelPropositoVida()       { return nivel(propositoVida); }
    public String getNivelCrecimientoPersonal() { return nivel(crecimientoPersonal); }
    public String getNivelBienestarGlobal()     { return nivel(bienestarGlobal); }

    private String nivel(Double v) {
        if (v == null) return "Sin datos";
        if (v >= 5.0)  return "Alto";
        if (v >= 3.5)  return "Medio";
        return "Bajo";
    }

    public int getPorcentajeAutoaceptacion()      { return pct(autoaceptacion); }
    public int getPorcentajeRelacionesPositivas() { return pct(relacionesPositivas); }
    public int getPorcentajeAutonomia()           { return pct(autonomia); }
    public int getPorcentajeDominioEntorno()      { return pct(dominioEntorno); }
    public int getPorcentajePropositoVida()       { return pct(propositoVida); }
    public int getPorcentajeCrecimientoPersonal() { return pct(crecimientoPersonal); }
    public int getPorcentajeBienestarGlobal()     { return pct(bienestarGlobal); }

    private int pct(Double v) {
        return v == null ? 0 : (int) Math.round((v / 6.0) * 100);
    }
}