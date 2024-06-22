package com.event.eventapp.service;

import com.event.eventapp.exceptions.TaskNotFoundException;
import com.event.eventapp.exceptions.UserNotFoundException;
import com.event.eventapp.exceptions.UserTaskAlreadyExistsException;
import com.event.eventapp.model.Task;
import com.event.eventapp.model.User;
import com.event.eventapp.model.UserTask;
import com.event.eventapp.repository.TaskRepository;
import com.event.eventapp.repository.UserRepository;
import com.event.eventapp.repository.UserTaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserTaskService {
    private final TaskRepository taskRepository;
    private final UserTaskRepository userTaskRepository;
    private final UserRepository userRepository;

    public UserTaskService(TaskRepository taskRepository, UserTaskRepository userTaskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userTaskRepository = userTaskRepository;
        this.userRepository = userRepository;
    }
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public List<UserTask> getUserTasksByUserId(Long loggedInUserId) {
        return userTaskRepository.findByUserId(loggedInUserId);
    }

    @Transactional
    public void addUserTask(Long userId, String taskName, String description) throws UserTaskAlreadyExistsException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Utilizator negăsit"));

        Optional<UserTask> existingUserTask = userTaskRepository.findByUserIdAndTaskName(userId, taskName);
        if (existingUserTask.isEmpty()) {
            UserTask userTask = new UserTask();
            userTask.setUser(user);
            userTask.setTaskName(taskName);
            userTask.setDescription(description);
            userTask.setStatus(false);
            userTask.setUuid(UUID.randomUUID().toString());
            userTaskRepository.save(userTask);
        } else {
            throw new UserTaskAlreadyExistsException("Există deja un task cu acest nume: " + taskName);
        }
    }

    public boolean existsByTaskName(String taskName) {
        return userTaskRepository.existsByTaskName(taskName);
    }

    @Transactional
    public void updateTask(String userTaskUuid, boolean status, boolean moveToToDo) {
        UserTask userTask = userTaskRepository.findByUuid(userTaskUuid)
                .orElseThrow(() -> new TaskNotFoundException("Task personal negăsit: " + userTaskUuid));

        userTask.setStatus(status);

        if (moveToToDo && !userTask.isStatus()) {
            userTask.setStatus(false);
        } else if (!moveToToDo && userTask.isStatus()) {
            userTask.setStatus(true);
        }

        userTaskRepository.save(userTask);
    }

    @Transactional
    public UserTask createUserTaskFromGeneralTask(Long userId, String generalTaskName, String generalTaskDescription, boolean status, String uuid) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Utilizator negăsit: " + userId));

        UserTask newUserTask = new UserTask();
        newUserTask.setUser(user);
        newUserTask.setTaskName(generalTaskName);
        newUserTask.setDescription(generalTaskDescription);
        newUserTask.setStatus(status);
        newUserTask.setUuid(uuid);
        userTaskRepository.save(newUserTask);
        return newUserTask;
    }

    @Transactional
    public void deleteUserTask(String uuid) {
        userTaskRepository.deleteByUuid(uuid);
    }

    public UserTask findUserTaskByUuidAndUserId(String uuid, Long userId) {
        return userTaskRepository.findByUuidAndUserId(uuid, userId)
                .orElseThrow(() -> new TaskNotFoundException("Task personal negăst."));
    }

    public Optional<UserTask> getUserTaskByUserIdAndTaskName(Long userId, String taskName) {
        return userTaskRepository.findByUserIdAndTaskName(userId, taskName);
    }
}
