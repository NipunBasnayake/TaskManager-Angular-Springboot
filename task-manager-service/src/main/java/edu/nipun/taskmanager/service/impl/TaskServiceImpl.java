package edu.nipun.taskmanager.service.impl;

import edu.nipun.taskmanager.dto.TaskDTO;
import edu.nipun.taskmanager.entity.Task;
import edu.nipun.taskmanager.exception.ResourceNotFoundException;
import edu.nipun.taskmanager.repository.TaskRepository;
import edu.nipun.taskmanager.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private static final String TASK_NOT_FOUND_MESSAGE = "Task not found with id: ";

    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(task -> modelMapper.map(task, TaskDTO.class))
                .toList();
    }

    @Override
    public TaskDTO getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TASK_NOT_FOUND_MESSAGE + id));
        return modelMapper.map(task, TaskDTO.class);
    }

    @Override
    public List<TaskDTO> getTasksByStatus(String status) {
        return taskRepository.findByStatus(status)
                .stream()
                .map(task -> modelMapper.map(task, TaskDTO.class))
                .toList();
    }

    @Override
    public TaskDTO createTask(TaskDTO taskDTO) {
        Task savedTask = taskRepository.save(modelMapper.map(taskDTO, Task.class));
        return modelMapper.map(savedTask, TaskDTO.class);
    }

    @Override
    public TaskDTO updateTask(Long id, TaskDTO taskDTO) {
        log.info(taskDTO.toString());

        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TASK_NOT_FOUND_MESSAGE + id));

        existingTask.setTitle(taskDTO.getTitle());
        existingTask.setDescription(taskDTO.getDescription());
        existingTask.setStatus(taskDTO.getStatus());

        Task updatedTask = taskRepository.save(existingTask);
        return modelMapper.map(updatedTask, TaskDTO.class);
    }

    @Override
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException(TASK_NOT_FOUND_MESSAGE + id);
        }
        taskRepository.deleteById(id);
    }
}