package com.event.eventapp.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TaskUpdateDTO {
        private boolean status;
        private boolean moveToToDo;
        private long taskId;
        private boolean isPersonal;
        private String uuid;

        public boolean getStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public boolean isMoveToToDo() {
            return moveToToDo;
        }

        public void setMoveToToDo(boolean moveToToDo) {
            this.moveToToDo = moveToToDo;
        }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    @JsonProperty("isPersonal")
    public boolean isPersonal() {
        return isPersonal;
    }

    public void setPersonal(boolean personal) {
        isPersonal = personal;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
