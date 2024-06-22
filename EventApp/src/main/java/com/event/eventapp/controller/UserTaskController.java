package com.event.eventapp.controller;

import com.event.eventapp.DTO.TaskDTO;
import com.event.eventapp.DTO.TaskUpdateDTO;
import com.event.eventapp.exceptions.TaskNotFoundException;
import com.event.eventapp.exceptions.UserTaskAlreadyExistsException;
import com.event.eventapp.model.Category;
import com.event.eventapp.model.Task;
import com.event.eventapp.model.UserTask;
import com.event.eventapp.service.CategoryService;
import com.event.eventapp.service.DefaultUserService;
import com.event.eventapp.service.TaskService;
import com.event.eventapp.service.UserTaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/tasks")
public class UserTaskController {
    private final UserTaskService userTaskService;
    private final DefaultUserService userService;
    private final TaskService taskService;
    private final CategoryService categoryService;

    public UserTaskController(UserTaskService userTaskService, DefaultUserService userService, TaskService taskService, CategoryService categoryService) {
        this.userTaskService = userTaskService;
        this.userService = userService;
        this.taskService = taskService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String showNotesPage(Model model, Principal principal) {
        Long loggedInUserId = userService.getUserIdByName(principal.getName());

        List<Task> tasks = userTaskService.getAllTasks();
        List<UserTask> userTasks = userTaskService.getUserTasksByUserId(loggedInUserId);

        List<UserTask> toDoTasks = userTasks.stream()
                .filter(task -> !task.isStatus())
                .collect(Collectors.toList());

        List<UserTask> doneTasks = userTasks.stream()
                .filter(UserTask::isStatus)
                .collect(Collectors.toList());

        List<Task> filteredTasks = tasks.stream()
                .filter(task -> doneTasks.stream().noneMatch(doneTask -> doneTask.getUuid().equals(task.getUuid())))
                .collect(Collectors.toList());

        model.addAttribute("loggedInUserId", loggedInUserId);
        model.addAttribute("toDoTasks", toDoTasks);
        model.addAttribute("doneTasks", doneTasks);
        model.addAttribute("tasks", filteredTasks);
        return "notes";
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<?> updateTaskStatus(@PathVariable String uuid, @RequestBody TaskUpdateDTO taskUpdate, Principal principal) {
        Task generalTask = taskService.findTaskByUuid(uuid);

        if (uuid == null || uuid.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("UUID invalid");
        }
        Long userId = userService.getUserIdByName(principal.getName());
        try {
            if (taskUpdate.isPersonal()) {
                UserTask userTask = userTaskService.findUserTaskByUuidAndUserId(uuid, userId);
                if (userTask == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task personal negăsit - UUID: " + uuid);
                }

                if (generalTask != null && generalTask.getUuid().equals(userTask.getUuid())) {
                    userTaskService.deleteUserTask(userTask.getUuid());
                    return ResponseEntity.ok().body("Mutare task general din lista personala.");
                }

                if (taskUpdate.getStatus()) {
                    userTaskService.updateTask(userTask.getUuid(), taskUpdate.getStatus(), taskUpdate.isMoveToToDo());
                } else {
                    userTaskService.updateTask(userTask.getUuid(), false, taskUpdate.isMoveToToDo());
                }
                return ResponseEntity.ok(userTask);
            } else {;
                if (generalTask == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task general negăsit - UUID: " + uuid);
                }

                if (taskUpdate.getStatus()) {
                    UserTask newUserTask = userTaskService.createUserTaskFromGeneralTask(userId, generalTask.getName(), generalTask.getDescription(), taskUpdate.getStatus(), taskUpdate.getUuid());
                    return ResponseEntity.ok(newUserTask);
                } else {
                    UserTask userTask = userTaskService.findUserTaskByUuidAndUserId(uuid, userId);
                    if (userTask != null) {
                        userTaskService.deleteUserTask(userTask.getUuid());
                        return ResponseEntity.ok().body("Mutare task general din lista personala.");
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task negăsit - UUID: " + uuid);
                    }
                }
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Eroare la actualizare task: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> addTaskAjax(Principal principal, @RequestBody TaskDTO taskDTO) {
        try {
            Long userId = userService.getUserIdByName(principal.getName());
            if (userTaskService.existsByTaskName(taskDTO.getTaskName())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Există un task cu acest nume!");
            }
            taskDTO.setUserId(userId);
            userTaskService.addUserTask(userId, taskDTO.getTaskName(), taskDTO.getDescription());
            Optional<UserTask> newUserTask = userTaskService.getUserTaskByUserIdAndTaskName(userId, taskDTO.getTaskName());
            if (newUserTask.isPresent()) {
                return ResponseEntity.ok(newUserTask.get());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Eroare la încărcare task nou.");
            }
        } catch (UserTaskAlreadyExistsException e) {
            return ResponseEntity.badRequest().body("Task-ul deja există: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Eroare la adăugarea task-ului: " + e.getMessage());
        }
    }

    @PostMapping("/remove")
    public ResponseEntity<?> removeTaskAjax(@RequestBody String userTaskUuid) {
        try {
            userTaskService.deleteUserTask(userTaskUuid);
            return ResponseEntity.ok("Task şters cu succes!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Eroare la ştergerea task-ului: " + e.getMessage());
        }
    }

    @ModelAttribute("allCategories")
    public List<Category> getAllCategories() {
        return categoryService.findAllCategories();
    }
}
