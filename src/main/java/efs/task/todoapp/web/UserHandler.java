package efs.task.todoapp.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import efs.task.todoapp.repository.UserEntity;
import efs.task.todoapp.service.ToDoService;
import efs.task.todoapp.util.HttpChecksUtil;
import efs.task.todoapp.util.MyExceptionUtil;


import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.logging.Logger;


public class UserHandler implements HttpHandler {
    private static final Logger LOGGER = Logger.getLogger(UserHandler.class.getName());
    final private ToDoService service;

    public UserHandler(ToDoService service){
        this.service = service;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException{
        HttpReturnServerStatus serverStatus;
        byte[] responseToServer = new byte[0];
        try{
            if(httpExchange.getRequestMethod().equals("POST")){
                //Add new User

                //Test Order
                //1.Check request body
                //2.Check given data (in setters)
                //3.Check if user exists

                final Map<String, String> userData = HttpChecksUtil.checkRequestBody(httpExchange);

                UserEntity userToAdd = new UserEntity();
                userToAdd.setUsername(userData.get("username"));
                userToAdd.setPassword(userData.get("password"));

                service.addUser(userToAdd);
                serverStatus = HttpReturnServerStatus.CODE_201;

            }
            else{
                throw new MyExceptionUtil(HttpReturnServerStatus.CODE_404, "Only POST method is allowed for /todo/user");
            }
        }
        catch(MyExceptionUtil e){
            serverStatus = e.getHttpStatus();
        }
        LOGGER.info("User Handler response:" + serverStatus);
        httpExchange.sendResponseHeaders(serverStatus.getErrorCode(), 0);

        final OutputStream responseBody = httpExchange.getResponseBody();
        responseBody.write(responseToServer);
        httpExchange.close();
    }
}
