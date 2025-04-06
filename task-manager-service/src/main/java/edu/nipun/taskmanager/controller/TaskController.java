package edu.nipun.taskmanager.controller;

import edu.nipun.taskmanager.dto.TaskDTO;
import edu.nipun.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Validated
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(
            @PathVariable @Min(value = 1, message = "ID must be positive") Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskDTO>> getTasksByStatus(
            @PathVariable @NotBlank @Pattern(
                    regexp = "^(TODO|IN_PROGRESS|COMPLETED)$",
                    message = "Status must be one of: TODO, IN_PROGRESS, COMPLETED"
            ) String status) {
        return ResponseEntity.ok(taskService.getTasksByStatus(status));
    }

    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody TaskDTO taskDTO) {
        return new ResponseEntity<>(taskService.createTask(taskDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(
            @PathVariable @Min(value = 1, message = "ID must be positive") Long id,
            @Valid @RequestBody TaskDTO taskDTO) {
        return ResponseEntity.ok(taskService.updateTask(id, taskDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable @Min(value = 1, message = "ID must be positive") Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
