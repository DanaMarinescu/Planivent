package com.event.eventapp.service;

import com.event.eventapp.model.Task;
import com.event.eventapp.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional
    public Task findTaskByUuid(String uuid) {
        return taskRepository.findByUuid(uuid);
    }
}
