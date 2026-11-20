package org.upiiz.service;

import org.upiiz.entities.Participante;
import org.upiiz.entities.Respuesta;
import org.upiiz.models.Evaluacion;
import org.upiiz.models.Resultado;
import org.upiiz.repository.ParticipanteRepository;
import org.upiiz.repository.RespuestaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BienestarService {

    private final ParticipanteRepository participanteRepository;
    private final RespuestaRepository respuestaRepository;

    private static final int TOTAL_PREGUNTAS = 39;
    private static final int ESCALA_MIN = 1;
    private static final int ESCALA_MAX = 6;

    public static final Map<Integer, String> PREGUNTAS = new LinkedHashMap<>();

    static {
        PREGUNTAS.put(1,  "Cuando repaso la historia de mi vida, estoy contento de cómo han resultado las cosas.");
        PREGUNTAS.put(2,  "A menudo me siento solo porque tengo pocos amigos íntimos con quien compartir mis preocupaciones.");
        PREGUNTAS.put(3,  "No tengo miedo de expresar mis opiniones, incluso cuando son opuestas a las opiniones de la mayoría de la gente.");
        PREGUNTAS.put(4,  "Me preocupa cómo otra gente evalúa las elecciones que he hecho en mi vida.");
        PREGUNTAS.put(5,  "Me resulta difícil dirigir mi vida hacia un camino que me satisfaga.");
        PREGUNTAS.put(6,  "En general, me siento seguro y positivo conmigo mismo.");
        PREGUNTAS.put(7,  "No tengo muchas personas que quieran escucharme cuando necesito hablar.");
        PREGUNTAS.put(8,  "Tiendo a preocuparme sobre lo que otra gente piensa de mí.");
        PREGUNTAS.put(9,  "Me juzgo por lo que yo creo que es importante, no por los valores que otros piensan que son importantes.");
        PREGUNTAS.put(10, "He sido capaz de construir un hogar y un modo de vida a mi gusto.");
        PREGUNTAS.put(11, "Soy una persona activa al realizar los proyectos que propuse para mí mismo.");
        PREGUNTAS.put(12, "Si tuviera la oportunidad, hay muchas cosas de mí mismo que cambiaría.");
        PREGUNTAS.put(13, "Siento que mis amistades me aportan muchas cosas.");
        PREGUNTAS.put(14, "Tiendo a estar influenciado por la gente con fuertes convicciones.");
        PREGUNTAS.put(15, "En general, siento que soy responsable de la situación en la que vivo.");
        PREGUNTAS.put(16, "Me siento bien cuando pienso en lo que he hecho en el pasado y lo que espero hacer en el futuro.");
        PREGUNTAS.put(17, "Mis objetivos en la vida han sido más una fuente de satisfacción que de frustración para mí.");
        PREGUNTAS.put(18, "Me gusta la mayor parte de los aspectos de mi personalidad.");
        PREGUNTAS.put(19, "Me parece que la mayor parte de las personas tienen más amigos que yo.");
        PREGUNTAS.put(20, "Tengo confianza en mis opiniones incluso si son contrarias al consenso general.");
        PREGUNTAS.put(21, "Las demandas de la vida diaria a menudo me deprimen.");
        PREGUNTAS.put(22, "Tengo clara la dirección y el objetivo de mi vida.");
        PREGUNTAS.put(23, "En general, con el tiempo siento que sigo aprendiendo más sobre mí mismo.");
        PREGUNTAS.put(24, "En muchos aspectos, me siento decepcionado de mis logros en la vida.");
        PREGUNTAS.put(25, "No he experimentado muchas relaciones cercanas y de confianza.");
        PREGUNTAS.put(26, "Es difícil para mí expresar mis propias opiniones en asuntos polémicos.");
        PREGUNTAS.put(27, "Soy bastante bueno manejando muchas de mis responsabilidades en la vida diaria.");
        PREGUNTAS.put(28, "No tengo claro qué es lo que intento conseguir en la vida.");
        PREGUNTAS.put(29, "Hace mucho tiempo que dejé de intentar hacer grandes mejoras o cambios en mi vida.");
        PREGUNTAS.put(30, "En su mayor parte, me siento orgulloso de quien soy y la vida que llevo.");
        PREGUNTAS.put(31, "Sé que puedo confiar en mis amigos, y ellos saben que pueden confiar en mí.");
        PREGUNTAS.put(32, "A menudo cambio mis decisiones si mis amigos o mi familia están en desacuerdo.");
        PREGUNTAS.put(33, "No quiero intentar nuevas formas de hacer las cosas; mi vida está bien como está.");
        PREGUNTAS.put(34, "Pienso que es importante tener nuevas experiencias que desafíen lo que uno piensa sobre sí mismo y sobre el mundo.");
        PREGUNTAS.put(35, "Cuando pienso en ello, realmente con los años no he mejorado mucho como persona.");
        PREGUNTAS.put(36, "Tengo la sensación de que con el tiempo me he desarrollado mucho como persona.");
        PREGUNTAS.put(37, "Para mí, la vida ha sido un proceso continuo de estudio, cambio y crecimiento.");
        PREGUNTAS.put(38, "Si me sintiera infeliz con mi situación de vida daría los pasos más eficaces para cambiarla.");
        PREGUNTAS.put(39, "Disfruto haciendo planes para el futuro y trabajar para hacerlos realidad.");
    }

    // Ítems redactados en sentido negativo (se invierten: 7 - valor en escala 1-6).
    private static final Set<Integer> PREGUNTAS_INVERSAS = Set.of(
            2, 4, 5, 7, 8, 12, 14, 19, 21, 24, 25, 26, 28, 29, 32, 33, 35
    );

    private static final Map<String, List<Integer>> DIMENSIONES = new LinkedHashMap<>();
    static {
        DIMENSIONES.put("AUTOACEPTACION",       List.of(1, 6, 12, 18, 24, 30));
        DIMENSIONES.put("RELACIONES_POSITIVAS", List.of(2, 7, 13, 19, 25, 31));
        DIMENSIONES.put("AUTONOMIA",            List.of(3, 4, 8, 9, 14, 20, 26, 32));
        DIMENSIONES.put("DOMINIO_ENTORNO",      List.of(5, 10, 15, 21, 27, 38));
        DIMENSIONES.put("PROPOSITO_VIDA",       List.of(11, 16, 17, 22, 28, 39));
        DIMENSIONES.put("CRECIMIENTO_PERSONAL", List.of(23, 29, 33, 34, 35, 36, 37));
    }

    // ------------------------------------------------------------------
    // Guardar evaluación nueva
    // ------------------------------------------------------------------
    @Transactional
    public Participante guardarEvaluacion(Evaluacion form) {

        validarRespuestas(form.getRespuestas());

        // Verificar duplicado en los últimos 5 minutos
        LocalDateTime hace5min = LocalDateTime.now().minusMinutes(5);
        long duplicados = participanteRepository.contarDuplicadosRecientes(
                form.getNombreCompleto().trim(),
                form.getGrupo().trim().toUpperCase(),
                form.getAnioEscolar(),
                hace5min
        );

        if (duplicados > 0) {
            return participanteRepository
                    .findTopByNombreCompletoAndGrupoOrderByFechaAplicacionDesc(
                            form.getNombreCompleto().trim(),
                            form.getGrupo().trim().toUpperCase()
                    ).orElseThrow();
        }

        Participante p = Participante.builder()
                .nombreCompleto(form.getNombreCompleto().trim())
                .sexo(form.getSexo())
                .edad(form.getEdad())
                .modalidad(form.getModalidad())
                .anioEscolar(form.getAnioEscolar())
                .grupo(form.getGrupo().trim().toUpperCase())
                .build();

        aplicarPromedios(p, form.getRespuestas());

        Participante guardado = participanteRepository.save(p);
        guardarRespuestas(guardado, form.getRespuestas());

        return guardado;
    }

    // ------------------------------------------------------------------
    // Recalcular registros que ya existían (datos viejos mal calculados)
    // ------------------------------------------------------------------
    @Transactional
    public int recalcularTodos() {
        List<Participante> participantes = participanteRepository.findAll();
        int recalculados = 0;

        for (Participante p : participantes) {
            List<Respuesta> actuales = respuestaRepository.findByParticipanteIdOrderByNumeroPreguntaAsc(p.getId());
            if (actuales.isEmpty()) {
                continue; // sin respuestas originales no se puede recalcular
            }

            Map<Integer, Integer> originales = new HashMap<>();
            for (Respuesta r : actuales) {
                originales.put(r.getNumeroPregunta(), r.getValorOriginal());
            }

            // Reemplazar respuestas con valorProcesado y dimensión correctos
            respuestaRepository.deleteAll(actuales);
            respuestaRepository.flush();
            guardarRespuestas(p, originales);

            // Reemplazar promedios del participante
            aplicarPromedios(p, originales);
            participanteRepository.save(p);

            recalculados++;
        }
        return recalculados;
    }

    // ------------------------------------------------------------------
    // Cálculos
    // ------------------------------------------------------------------
    public Map<String, Double> calcularPromedios(Map<Integer, Integer> respuestas) {
        Map<String, Double> promedios = new LinkedHashMap<>();
        DIMENSIONES.forEach((nombre, ids) -> {
            List<Double> valores = new ArrayList<>();
            for (Integer id : ids) {
                Integer v = respuestas.get(id);
                if (v != null) {
                    valores.add((double) procesar(id, v));
                }
            }
            double avg = valores.isEmpty() ? 0.0
                    : valores.stream().mapToDouble(d -> d).average().orElse(0.0);
            promedios.put(nombre, redondear(avg));
        });
        return promedios;
    }

    /** Aplica los 6 promedios y el global al participante. */
    private void aplicarPromedios(Participante p, Map<Integer, Integer> respuestas) {
        Map<String, Double> proms = calcularPromedios(respuestas);
        p.setPromedioAutoaceptacion(proms.get("AUTOACEPTACION"));
        p.setPromedioRelacionesPositivas(proms.get("RELACIONES_POSITIVAS"));
        p.setPromedioAutonomia(proms.get("AUTONOMIA"));
        p.setPromedioDominioEntorno(proms.get("DOMINIO_ENTORNO"));
        p.setPromedioPropositoVida(proms.get("PROPOSITO_VIDA"));
        p.setPromedioCrecimientoPersonal(proms.get("CRECIMIENTO_PERSONAL"));

        double global = proms.values().stream()
                .mapToDouble(Double::doubleValue)
                .average().orElse(0.0);
        p.setPromedioBienestarGlobal(redondear(global));
    }

    private void guardarRespuestas(Participante p, Map<Integer, Integer> respuestas) {
        List<Respuesta> lista = new ArrayList<>();
        respuestas.forEach((num, val) -> lista.add(Respuesta.builder()
                .participante(p)
                .numeroPregunta(num)
                .valorOriginal(val)
                .valorProcesado(procesar(num, val))
                .dimension(getDimension(num))
                .build()));
        respuestaRepository.saveAll(lista);
    }

    /** Invierte el valor si el ítem es inverso (escala 1-6 → 7 - valor). */
    private int procesar(int numeroPregunta, int valor) {
        return PREGUNTAS_INVERSAS.contains(numeroPregunta)
                ? (ESCALA_MIN + ESCALA_MAX) - valor
                : valor;
    }

    private void validarRespuestas(Map<Integer, Integer> respuestas) {
        if (respuestas == null || respuestas.size() != TOTAL_PREGUNTAS) {
            throw new IllegalArgumentException(
                    "Se requieren las " + TOTAL_PREGUNTAS + " respuestas.");
        }
        respuestas.forEach((num, val) -> {
            if (num == null || num < 1 || num > TOTAL_PREGUNTAS) {
                throw new IllegalArgumentException("Número de pregunta inválido: " + num);
            }
            if (val == null || val < ESCALA_MIN || val > ESCALA_MAX) {
                throw new IllegalArgumentException(
                        "La respuesta a la pregunta " + num + " debe estar entre "
                                + ESCALA_MIN + " y " + ESCALA_MAX + ".");
            }
        });
    }

    public Resultado toResultado(Participante p) {
        return Resultado.builder()
                .participanteId(p.getId())
                .nombreCompleto(p.getNombreCompleto())
                .sexo(p.getSexo())
                .edad(p.getEdad())
                .modalidad(p.getModalidad())
                .anioEscolar(p.getAnioEscolar())
                .grupo(p.getGrupo())
                .autoaceptacion(p.getPromedioAutoaceptacion())
                .relacionesPositivas(p.getPromedioRelacionesPositivas())
                .autonomia(p.getPromedioAutonomia())
                .dominioEntorno(p.getPromedioDominioEntorno())
                .propositoVida(p.getPromedioPropositoVida())
                .crecimientoPersonal(p.getPromedioCrecimientoPersonal())
                .bienestarGlobal(p.getPromedioBienestarGlobal())
                .build();
    }

    private String getDimension(int n) {
        return DIMENSIONES.entrySet().stream()
                .filter(e -> e.getValue().contains(n))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("OTRO");
    }

    private double redondear(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}