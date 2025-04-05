package edu.nipun.taskmanager.repository;

import edu.nipun.taskmanager.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Collection<Object> findByStatus(String status);
}
