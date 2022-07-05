package efs.task.todoapp.web;

import com.sun.net.httpserver.HttpExchange;
import efs.task.todoapp.repository.TaskEntity;
import efs.task.todoapp.repository.UserEntity;
import efs.task.todoapp.service.ToDoService;
import efs.task.todoapp.util.MyExceptionUtil;
import efs.task.todoapp.util.HttpChecksUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpHandler;


public class TaskHandler implements HttpHandler {
    private static final Logger LOGGER = Logger.getLogger(TaskHandler.class.getName());
    final private ToDoService service;

    public TaskHandler(ToDoService service){
        this.service = service;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException{
        HttpReturnServerStatus serverStatus = HttpReturnServerStatus.CODE_200;
        byte[] responseToServer = new byte[0];
        try{
            String[] userData = HttpChecksUtil.getAuthHeaderData(httpExchange.getRequestHeaders());

            switch (httpExchange.getRequestMethod()){
                case "POST":{
                    //Add new Task
                    LOGGER.info("Task Handler: POST");
                    //Test order
                    //1.Body check
                    //2.User check

                    final Map<String, String> taskData = HttpChecksUtil.checkRequestBody(httpExchange);

                    TaskEntity taskToAdd = new TaskEntity();
                    taskToAdd.setDescription(taskData.get("description"));
                    taskToAdd.setDue(taskData.get("due"));

                    final UserEntity tempUser = HttpChecksUtil.checkUser(userData, service);

                    service.addTask(taskToAdd, tempUser.getUsername());
                    responseToServer = taskToAdd.taskToJson().getBytes(StandardCharsets.UTF_8);
                    serverStatus = HttpReturnServerStatus.CODE_201;
                }
                break;

                case "GET":{
                    //Get the task
                    LOGGER.info("Task Handler: GET");
                    //Test order
                    //1.User check
                    //2.Task check
                    //3a.User = Author check

                    final UUID taskUUID = HttpChecksUtil.getTaskUUID(httpExchange.getRequestURI().getPath(),true);

                    final UserEntity tempUser = HttpChecksUtil.checkUser(userData, service);

                    TaskEntity tempTask = null;
                    if(taskUUID != null){
                        tempTask = service.findTask(taskUUID);
                        if (tempTask == null) {
                            throw new MyExceptionUtil(HttpReturnServerStatus.CODE_404, "Wrong TaskID");
                        }
                    }

                    //Get single Task
                    if(tempTask != null){

                        String tempTaskUsername = tempTask.getUsername();
                        String tempUserUsername = tempUser.getUsername();
                        if(tempTaskUsername == null || tempTaskUsername.isBlank() || tempTask.getUUID() == null){
                            throw new MyExceptionUtil(HttpReturnServerStatus.CODE_403, "Problem with author of task (GET)");
                        }
                        if(!tempTaskUsername.equals(tempUserUsername)){
                            throw new MyExceptionUtil(HttpReturnServerStatus.CODE_403, "User is not author of task");
                        }

                        responseToServer = tempTask.taskToJson().getBytes(StandardCharsets.UTF_8);
                    }
                    else{
                        //Get all Tasks

                        final List<TaskEntity> listOfTasks = service.findTaskOfUser(tempUser.getUsername());

                        StringBuilder stringBuilder = new StringBuilder("[");
                        for(TaskEntity userTask : listOfTasks){
                            stringBuilder.append(userTask.taskToJson()).append(",");
                        }
                        stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
                        stringBuilder.append("]");

                        responseToServer = stringBuilder.toString().getBytes(StandardCharsets.UTF_8);
                    }
                    serverStatus = HttpReturnServerStatus.CODE_200;
                }
                break;

                case "PUT":{
                    //Modify task
                    LOGGER.info("Task Handler: PUT");
                    //Test Order
                    //1.Body check
                    //2.Get TaskUUID
                    //3.User check
                    //4.Task check
                    //5.User = Author check
                    //6.New Data check (in setters)

                    final Map<String, String> taskData = HttpChecksUtil.checkRequestBody(httpExchange);

                    final UUID taskID = HttpChecksUtil.getTaskUUID(httpExchange.getRequestURI().getPath(),false );

                    final UserEntity tempUser = HttpChecksUtil.checkUser(userData, service);

                    TaskEntity tempTask = service.findTask(taskID);
                    if(tempTask == null) {
                        throw new MyExceptionUtil(HttpReturnServerStatus.CODE_404, "Wrong TaskUUID");
                    }

                    String tempTaskUsername = tempTask.getUsername();
                    String tempUserUsername = tempUser.getUsername();
                    if(tempTaskUsername == null || tempTaskUsername.isBlank() || tempTask.getUUID() == null){
                        throw new MyExceptionUtil(HttpReturnServerStatus.CODE_403, "Problem with author of task (PUT)");
                    }
                    if(!tempTaskUsername.equals(tempUserUsername)){
                        throw new MyExceptionUtil(HttpReturnServerStatus.CODE_403, "User is not author of task");
                    }

                    TaskEntity taskToUpdate = new TaskEntity();
                    taskToUpdate.setDescription(taskData.get("description"));
                    taskToUpdate.setDue(taskData.get("due"));

                    tempTask = service.updateTask(taskID, taskToUpdate);
                    taskToUpdate.setUUID(tempTask.getUUID());
                    responseToServer = taskToUpdate.taskToJson().getBytes();
                }
                break;

                case "DELETE":{
                    //Delete Task
                    LOGGER.info("Task Handler DELETE");
                    //Test Order
                    //1.Get TaskUUID
                    //1.User Check
                    //2.Task Check
                    //3.User = Author Check

                    final UUID taskUUID = HttpChecksUtil.getTaskUUID(httpExchange.getRequestURI().getPath(), false);

                    final UserEntity tempUser = HttpChecksUtil.checkUser(userData, service);

                    TaskEntity tempTask = service.findTask(taskUUID);
                    if(null == tempTask) {
                        throw new MyExceptionUtil(HttpReturnServerStatus.CODE_404, "Wrong Task UUID");
                    }

                    String tempTaskUsername = tempTask.getUsername();
                    String tempUserUsername = tempUser.getUsername();
                    if(tempTaskUsername == null || tempTaskUsername.isBlank() || tempTask.getUUID() == null){
                        throw new MyExceptionUtil(HttpReturnServerStatus.CODE_403, "Problem with author of task (PUT)");
                    }
                    if(!tempTaskUsername.equals(tempUserUsername)){
                        throw new MyExceptionUtil(HttpReturnServerStatus.CODE_403, "User is not author of task");
                    }

                    service.removeTask(taskUUID);
                    serverStatus = HttpReturnServerStatus.CODE_200;
                }
                break;
                default:{
                    throw new MyExceptionUtil(HttpReturnServerStatus.CODE_404, "Unknown HTTP Method for Task");
                }
            }
        }
        catch(MyExceptionUtil e){
            serverStatus = e.getHttpStatus();
        }
        LOGGER.info("Task Handler, response code" + serverStatus);
        httpExchange.sendResponseHeaders(serverStatus.getErrorCode(), responseToServer.length);

        final OutputStream responseBody = httpExchange.getResponseBody();
        responseBody.write(responseToServer);
        LOGGER.info("Task Handler, response" + new String(responseToServer));
        httpExchange.close();
    }

}
