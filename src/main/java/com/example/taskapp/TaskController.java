
package com.example.taskapp;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping
    public List<Task> list() {
        return service.findAll();
    }

    @PostMapping
    public ResponseEntity<Task> create(@RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.status(201).body(service.create(body.get("title")));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
@PutMapping("/{id}/done")
    public ResponseEntity<Task> done(@PathVariable long id) {
        return service.markDone(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        return service.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
} 
