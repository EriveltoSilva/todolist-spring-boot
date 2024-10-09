package com.eriveltosilva.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eriveltosilva.todolist.utils.Utils;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @PutMapping("/{taskId}")
    public ResponseEntity update(@PathVariable UUID taskId, @RequestBody TaskModel taskModel,
            HttpServletRequest request) {
        var task = this.taskRepository.findById(taskId).orElse(null);

        if (task == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarefa não encontrada!");

        Utils.copyNonNullProperties(taskModel, task);

        UUID userId = (UUID) request.getAttribute("userId");
        if (!userId.equals(task.getUserId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Usuário não tem permissão para alterar essa tarefa!");

        return ResponseEntity.status(HttpStatus.OK).body(this.taskRepository.save(task));
    }

    @GetMapping("/")
    public ResponseEntity<List<TaskModel>> list(HttpServletRequest request) {
        return ResponseEntity.ok(taskRepository.findByUserId((UUID) request.getAttribute("userId")));
    }

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        taskModel.setUserId((UUID) request.getAttribute("userId"));
        LocalDateTime currentDate = LocalDateTime.now();
        if (currentDate.isAfter(taskModel.getStartAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("A data de inicio deve ser maior que a data actual!");
        }
        if (currentDate.isAfter(taskModel.getEndAt()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("A data de termino deve ser maior que a data actual!");
        if (taskModel.getEndAt().isBefore(taskModel.getStartAt()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("A data de termino deve vir depois da data de inicio!");
        return ResponseEntity.status(HttpStatus.CREATED).body(taskRepository.save(taskModel));
    }
}

/*
 * {
 * "timestamp": "2024-10-09T13:30:41.890+00:00",
 * "status": 401,
 * "error": "Unauthorized",
 * "message": "Usuário não encontrado!",
 * "path": "/tasks/"
 * }
 */