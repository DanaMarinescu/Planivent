
$(document).ready(function() {
    $('.form-box').submit(function(e) {
        e.preventDefault();
        var taskName = $('#taskName').val();
        var description = $('#description').val();
        addTask(taskName, description);
    });

    $('.task-checkbox').change(function() {
        updateTaskStatusFromAttribute(this);
    });
});

function addTask(taskName, description) {
    const csrfToken = $("meta[name='_csrf']").attr("content");
    const csrfHeader = $("meta[name='_csrf_header']").attr("content");

    $.ajax({
        type: "POST",
        url: "/tasks",
        contentType: "application/json",
        data: JSON.stringify({
            taskName: taskName,
            description: description
        }),
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        success: function(task) {
            console.log("Task adăugat cu succes: ", task);
            if (task && task.uuid && task.taskName && task.description) {
                addTaskToDOM(task);
            } else {
                console.error("Task-ul nu are date corespunzătoare: ", task);
            }
        },
        error: function(xhr, status, error) {
            if (xhr.status === 400 && xhr.responseText.includes("Există")) {
                alert("Există deja un task cu acest nume. Încercaţi alt nume.");
            } else {
                alert("Eroare la adăugare task: " + xhr.responseText);
            }
        }
    });
}

function addTaskToDOM(task) {
    console.log("Task object:", task);
    const toDoList = $('.to-do-list ul');
    const doneList = $('.done-list ul');

    const targetList = task.status ? doneList : toDoList;

    const taskHtml = `
        <li id="todo-task-user-task-${task.uuid}">
            <input type="checkbox" id="toDoUserTaskCheckbox${task.uuid}"
                   data-uuid="${task.uuid}" data-is-personal="true"
                   class="task-checkbox" ${task.status ? 'checked' : ''}
                   onchange="updateTaskStatusFromAttribute(this)">
            <label for="toDoUserTaskCheckbox${task.uuid}" title="${task.description}">
            ${task.taskName}
            </label>
        </li>
    `;

    targetList.append(taskHtml);
}

function updateTaskStatusFromAttribute(element) {
    const taskUuid = $(element).data("uuid");
    const isChecked = $(element).is(":checked");
    const isPersonalTask = $(element).data("is-personal");

    updateTaskStatus(taskUuid, isChecked, isPersonalTask);
}

function updateTaskStatus(taskUuid, isChecked, isPersonalTask) {
    const csrfToken = $("meta[name='_csrf']").attr("content");
    const csrfHeader = $("meta[name='_csrf_header']").attr("content");

    $.ajax({
        type: "PUT",
        url: "/tasks/" + taskUuid,
        contentType: "application/json",
        data: JSON.stringify({
            uuid: taskUuid,
            status: isChecked,
            isPersonal: isPersonalTask
        }),
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        success: function(response) {
            if (response === "Mutare task general din lista personala.") {
                location.reload();
            } else {
                relocateTask(taskUuid, isChecked, isPersonalTask);
            }
        },
        error: function(xhr, status, error) {
            console.error("Eroare la schimbare listă: ", xhr.responseText);
        }
    });
}

function relocateTask(taskUuid, isChecked, isPersonalTask) {
    var taskPrefix = isPersonalTask ? 'user-task-' : 'general-task-';
    var originalListPrefix = isChecked ? 'todo-task-' : 'done-task-';
    var newListPrefix = isChecked ? 'done-task-' : 'todo-task-';
    var fullOriginalId = originalListPrefix + taskPrefix + taskUuid;
    var taskElement = document.getElementById(fullOriginalId);

    if (isChecked) {
        document.querySelector('.done-list ul').appendChild(taskElement);
    } else {
        var targetListSelector = isPersonalTask ? '.to-do-list ul' : '.general-list ul';
        document.querySelector(targetListSelector).appendChild(taskElement);
    }

    taskElement.id = newListPrefix + taskPrefix + taskUuid;
}