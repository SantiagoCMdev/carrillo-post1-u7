package com.universidad.tareas.controller;

import com.universidad.tareas.model.Prioridad;
import com.universidad.tareas.model.Tarea;
import com.universidad.tareas.service.TareaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tareas")
public class TareaApiController {

    private final TareaService servicio;

    public TareaApiController(TareaService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public ResponseEntity<List<Tarea>> listar(
            @RequestParam(required = false) Prioridad prioridad,
            @RequestParam(required = false) Boolean completada) {
        return ResponseEntity.ok(servicio.filtrar(prioridad, completada));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tarea> buscar(@PathVariable Long id) {
        return servicio.buscarPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Tarea> crear(@Valid @RequestBody Tarea tarea) {
        Tarea creada = servicio.guardar(tarea);
        URI ubicacion = URI.create("/api/tareas/" + creada.getId());
        return ResponseEntity.created(ubicacion).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tarea> actualizar(@PathVariable Long id, @Valid @RequestBody Tarea tarea) {
        return servicio.buscarPorId(id)
            .map(existente -> {
                tarea.setId(id);
                return ResponseEntity.ok(servicio.guardar(tarea));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/completar")
    public ResponseEntity<Tarea> completar(@PathVariable Long id) {
        return servicio.marcarCompletada(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!servicio.eliminar(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}