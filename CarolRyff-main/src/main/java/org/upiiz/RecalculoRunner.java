package org.upiiz;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.upiiz.service.BienestarService;

/**
 * Recalcula los promedios y respuestas procesadas de los registros existentes.
 * Solo se ejecuta si arrancas la app con:  --bienestar.recalcular=true
 * (o bienestar.recalcular=true en application.properties, y luego lo quitas).
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "bienestar.recalcular", havingValue = "true")
public class RecalculoRunner implements ApplicationRunner {

    private final BienestarService bienestarService;

    @Override
    public void run(ApplicationArguments args) {
        int n = bienestarService.recalcularTodos();
        log.info("Recálculo terminado: {} participantes actualizados.", n);
    }
}