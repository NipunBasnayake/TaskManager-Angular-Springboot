package edu.nipun.taskmanager.service.impl;

import edu.nipun.taskmanager.dto.TaskDTO;
import edu.nipun.taskmanager.entity.Task;
import edu.nipun.taskmanager.exception.ResourceNotFoundException;
import edu.nipun.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task task1;
    private Task task2;
    private TaskDTO taskDTO1;
    private TaskDTO taskDTO2;
    private TaskDTO inputTaskDTO;
    private final Long taskId1 = 1L;

    @BeforeEach
    void setUp() {
        task1 = new Task();
        task1.setId(taskId1);
        task1.setTitle("Task 1");
        task1.setDescription("Description for task 1");
        task1.setStatus("TODO");
        task1.setCreatedAt(LocalDateTime.now().minusDays(1));

        task2 = new Task();
        Long taskId2 = 2L;
        task2.setId(taskId2);
        task2.setTitle("Task 2");
        task2.setDescription("Description for task 2");
        task2.setStatus("IN_PROGRESS");
        task2.setCreatedAt(LocalDateTime.now().minusHours(5));

        taskDTO1 = new TaskDTO();
        taskDTO1.setId(taskId1);
        taskDTO1.setTitle("Task 1");
        taskDTO1.setDescription("Description for task 1");
        taskDTO1.setStatus("TODO");
        taskDTO1.setCreatedAt(task1.getCreatedAt());

        taskDTO2 = new TaskDTO();
        taskDTO2.setId(taskId2);
        taskDTO2.setTitle("Task 2");
        taskDTO2.setDescription("Description for task 2");
        taskDTO2.setStatus("IN_PROGRESS");
        taskDTO2.setCreatedAt(task2.getCreatedAt());

        inputTaskDTO = new TaskDTO();
        inputTaskDTO.setTitle("New Task");
        inputTaskDTO.setDescription("Description for new task");
        inputTaskDTO.setStatus("TODO");
    }

    @Test
    @DisplayName("Get all tasks successfully")
    void getAllTasks() {
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task1, task2));
        when(modelMapper.map(task1, TaskDTO.class)).thenReturn(taskDTO1);
        when(modelMapper.map(task2, TaskDTO.class)).thenReturn(taskDTO2);

        List<TaskDTO> result = taskService.getAllTasks();

        assertEquals(2, result.size());
        assertEquals(taskDTO1.getId(), result.get(0).getId());
        assertEquals(taskDTO2.getId(), result.get(1).getId());
        assertEquals(taskDTO1.getTitle(), result.get(0).getTitle());
        assertEquals(taskDTO2.getTitle(), result.get(1).getTitle());

        verify(taskRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(task1, TaskDTO.class);
        verify(modelMapper, times(1)).map(task2, TaskDTO.class);
    }

    @Test
    @DisplayName("Get task by ID successfully")
    void getTaskById_Success() {
        when(taskRepository.findById(taskId1)).thenReturn(Optional.of(task1));
        when(modelMapper.map(task1, TaskDTO.class)).thenReturn(taskDTO1);

        TaskDTO result = taskService.getTaskById(taskId1);

        assertEquals(taskDTO1.getId(), result.getId());
        assertEquals(taskDTO1.getTitle(), result.getTitle());
        assertEquals(taskDTO1.getStatus(), result.getStatus());

        verify(taskRepository, times(1)).findById(taskId1);
        verify(modelMapper, times(1)).map(task1, TaskDTO.class);
    }

    @Test
    @DisplayName("Get task by non-existent ID throws exception")
    void getTaskById_NonExistentId_ThrowsException() {
        Long nonExistentId = 999L;
        when(taskRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> taskService.getTaskById(nonExistentId));

        assertEquals("Task not found with id: " + nonExistentId, exception.getMessage());

        verify(taskRepository, times(1)).findById(nonExistentId);
        verifyNoInteractions(modelMapper);
    }

    @Test
    @DisplayName("Get tasks by status successfully")
    void getTasksByStatus() {
        String taskStatus = "IN_PROGRESS";
        when(taskRepository.findByStatus(taskStatus)).thenReturn(List.of(task2));
        when(modelMapper.map(task2, TaskDTO.class)).thenReturn(taskDTO2);

        List<TaskDTO> result = taskService.getTasksByStatus(taskStatus);

        assertEquals(1, result.size());
        assertEquals(taskDTO2.getId(), result.get(0).getId());
        assertEquals(taskDTO2.getStatus(), result.get(0).getStatus());
        assertEquals(taskStatus, result.get(0).getStatus());

        verify(taskRepository, times(1)).findByStatus(taskStatus);
        verify(modelMapper, times(1)).map(task2, TaskDTO.class);
    }

    @Test
    @DisplayName("Create task successfully")
    void createTask_Success() {
        Task newTask = new Task();
        newTask.setTitle(inputTaskDTO.getTitle());
        newTask.setDescription(inputTaskDTO.getDescription());
        newTask.setStatus(inputTaskDTO.getStatus());

        Task savedTask = new Task();
        savedTask.setId(3L);
        savedTask.setTitle(inputTaskDTO.getTitle());
        savedTask.setDescription(inputTaskDTO.getDescription());
        savedTask.setStatus(inputTaskDTO.getStatus());
        savedTask.setCreatedAt(LocalDateTime.now());

        TaskDTO savedTaskDTO = new TaskDTO();
        savedTaskDTO.setId(3L);
        savedTaskDTO.setTitle(inputTaskDTO.getTitle());
        savedTaskDTO.setDescription(inputTaskDTO.getDescription());
        savedTaskDTO.setStatus(inputTaskDTO.getStatus());
        savedTaskDTO.setCreatedAt(savedTask.getCreatedAt());

        when(modelMapper.map(inputTaskDTO, Task.class)).thenReturn(newTask);
        when(taskRepository.save(newTask)).thenReturn(savedTask);
        when(modelMapper.map(savedTask, TaskDTO.class)).thenReturn(savedTaskDTO);

        TaskDTO result = taskService.createTask(inputTaskDTO);

        assertEquals(savedTaskDTO.getId(), result.getId());
        assertEquals(savedTaskDTO.getTitle(), result.getTitle());
        assertEquals(savedTaskDTO.getStatus(), result.getStatus());

        verify(modelMapper, times(1)).map(inputTaskDTO, Task.class);
        verify(taskRepository, times(1)).save(newTask);
        verify(modelMapper, times(1)).map(savedTask, TaskDTO.class);
    }

    @Test
    @DisplayName("Update task successfully")
    void updateTask_Success() {
        inputTaskDTO.setId(taskId1);

        Task existingTask = new Task();
        existingTask.setId(taskId1);
        existingTask.setTitle("Old Title");
        existingTask.setDescription("Old Description");
        existingTask.setStatus("TODO");
        existingTask.setCreatedAt(LocalDateTime.now().minusDays(1));

        Task updatedTask = new Task();
        updatedTask.setId(taskId1);
        updatedTask.setTitle(inputTaskDTO.getTitle());
        updatedTask.setDescription(inputTaskDTO.getDescription());
        updatedTask.setStatus(inputTaskDTO.getStatus());
        updatedTask.setCreatedAt(existingTask.getCreatedAt());

        TaskDTO updatedTaskDTO = new TaskDTO();
        updatedTaskDTO.setId(taskId1);
        updatedTaskDTO.setTitle(inputTaskDTO.getTitle());
        updatedTaskDTO.setDescription(inputTaskDTO.getDescription());
        updatedTaskDTO.setStatus(inputTaskDTO.getStatus());
        updatedTaskDTO.setCreatedAt(existingTask.getCreatedAt());

        when(taskRepository.findById(taskId1)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);
        when(modelMapper.map(updatedTask, TaskDTO.class)).thenReturn(updatedTaskDTO);

        TaskDTO result = taskService.updateTask(taskId1, inputTaskDTO);

        assertEquals(updatedTaskDTO.getId(), result.getId());
        assertEquals(updatedTaskDTO.getTitle(), result.getTitle());
        assertEquals(updatedTaskDTO.getDescription(), result.getDescription());
        assertEquals(updatedTaskDTO.getStatus(), result.getStatus());

        verify(taskRepository, times(1)).findById(taskId1);
        verify(taskRepository, times(1)).save(any(Task.class));
        verify(modelMapper, times(1)).map(updatedTask, TaskDTO.class);
    }

    @Test
    @DisplayName("Update task with non-existent ID throws exception")
    void updateTask_NonExistentId_ThrowsException() {
        Long nonExistentId = 999L;

        when(taskRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> taskService.updateTask(nonExistentId, inputTaskDTO));

        assertEquals("Task not found with id: " + nonExistentId, exception.getMessage());

        verify(taskRepository, times(1)).findById(nonExistentId);
        verifyNoMoreInteractions(taskRepository, modelMapper);
    }

    @Test
    @DisplayName("Delete task successfully")
    void deleteTask_Success() {
        when(taskRepository.existsById(taskId1)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(taskId1);

        taskService.deleteTask(taskId1);

        verify(taskRepository, times(1)).existsById(taskId1);
        verify(taskRepository, times(1)).deleteById(taskId1);
    }

    @Test
    @DisplayName("Delete task with non-existent ID throws exception")
    void deleteTask_NonExistentId_ThrowsException() {
        Long nonExistentId = 999L;
        when(taskRepository.existsById(nonExistentId)).thenReturn(false);

        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> taskService.deleteTask(nonExistentId));

        assertEquals("Task not found with id: " + nonExistentId, exception.getMessage());

        verify(taskRepository, times(1)).existsById(nonExistentId);
        verify(taskRepository, never()).deleteById(anyLong());
    }
}