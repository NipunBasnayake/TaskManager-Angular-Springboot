package edu.nipun.taskmanager.service;

import edu.nipun.taskmanager.dto.TaskDTO;

import java.util.List;

public interface TaskService {
    List<TaskDTO> getAllTasks();
    TaskDTO getTaskById(Long id);
    List<TaskDTO> getTasksByStatus(String status);
    TaskDTO createTask(TaskDTO taskDTO);
    TaskDTO updateTask(Long id, TaskDTO taskDTO);
    void deleteTask(Long id);
}
