package efs.task.todoapp.repository;

import efs.task.todoapp.util.MyExceptionUtil;
import efs.task.todoapp.web.HttpReturnServerStatus;

import java.text.SimpleDateFormat;
import java.util.UUID;

public class TaskEntity {
    private UUID uuid;
    private String username;
    private String description;
    private String due;

    public TaskEntity() {}

    public UUID getUUID(){
        return uuid;
    }
    public void setUUID(UUID newUUID){
        this.uuid = newUUID;
    }

    public String getUsername(){
        return username;
    }
    public void setUsername(String newUsername){
        this.username = newUsername;
    }

    public String getDescription(){
        return description;
    }
    public void setDescription(String newDescription){
        this.description = newDescription;
    }

    public String getDue(){return due;}
    public void setDue(String newDue) throws MyExceptionUtil {
        this.due = newDue;
    }

    public String taskToJson(){
        String taskUUID = "", taskDesc = "", taskDue = "";
        if(uuid != null){taskUUID = uuid.toString();}
        if(description != null){taskDesc = description;}
        if(due != null){taskDue = due;}

        StringBuilder responseToServer = new StringBuilder("{");

        if(taskUUID.length() > 0){
            responseToServer.append("\"id\":\"").append(taskUUID).append("\"");
            if(taskDesc.length() > 0){
                responseToServer.append(",\"description\":\"").append(taskDesc).append("\"");
            }
            if(taskDue.length() > 0){
                responseToServer.append(",\"due\":\"").append(taskDue).append("\"");
            }
        }
        return responseToServer.append("}").toString();

    }
}
