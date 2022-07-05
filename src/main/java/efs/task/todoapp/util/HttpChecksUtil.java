package efs.task.todoapp.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import efs.task.todoapp.Base64Utils;
import efs.task.todoapp.repository.UserEntity;
import efs.task.todoapp.web.HttpReturnServerStatus;
import efs.task.todoapp.service.ToDoService;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class HttpChecksUtil {

    private static final Logger LOGGER = Logger.getLogger(HttpChecksUtil.class.getName());

    public static Map<String, String> checkRequestBody(HttpExchange httpExchange) throws IOException, MyExceptionUtil {
        //Read Request Body

        final InputStream requestBody = httpExchange.getRequestBody();
        final String body = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);

        LOGGER.info("Request body" + body);
        try {
            final Type mapType = new TypeToken<Map<String, String>>() {
            }.getType();
            Map<String, String> bodyJsonData = new Gson().fromJson(body, mapType);
            if (bodyJsonData == null ){
                throw new Exception();
            }
            return bodyJsonData;
        }
        catch(Exception e){
            throw new MyExceptionUtil(HttpReturnServerStatus.CODE_400, "Request Body not in JSON");
        }
    }

    public static UUID getTaskUUID(String address, boolean allTasks) throws MyExceptionUtil {
        //Trim the Task UUID from address text
        try {
            if(address.length() < 11){ //GET want to return all tasks
                if(allTasks){
                    return null;
                }
                throw new Exception();
            }
            LOGGER.info("Task Handler, single Task: TaskUUID" + UUID.fromString(address.substring(11)));
            return UUID.fromString(address.substring(11));
        }
        catch(Exception e){
            throw new MyExceptionUtil(HttpReturnServerStatus.CODE_400, "Wrong Task UUID");
        }
    }

    public static String[] getAuthHeaderData(Headers headers){
        //Get the auth String from Header
        String authHeaderData;

        try {
            List<String> listOfHeaders = headers.get("auth");

            if (listOfHeaders == null) {
                throw new Exception();
            }
            authHeaderData = listOfHeaders.get(0);
            if (authHeaderData == null || authHeaderData.isBlank()) {
                throw new Exception();
            }
        }
        catch(Exception e){
            throw new MyExceptionUtil(HttpReturnServerStatus.CODE_400, "Error in auth data ");
        }
        LOGGER.info("Task Handler auth" + authHeaderData);

        String[] temp = authHeaderData.split(":");
        String[] decodedAuthData = new String[2];
        try{
            if(temp[0] == null || temp[0].isBlank()){
                throw new Exception();
            }
            decodedAuthData[0] = Base64Utils.decode(temp[0]);
            if(temp[1] == null || temp[1].isBlank()){
                throw new Exception();
            }
            decodedAuthData[1] = Base64Utils.decode(temp[1]);
        }
        catch(Exception e){
            throw new MyExceptionUtil(HttpReturnServerStatus.CODE_400, "Error in decoding auth data");
        }

        return decodedAuthData;
    }

    public static UserEntity checkUser(String[] userData, ToDoService service) throws MyExceptionUtil {
        //User check

        String username = userData[0];
        String password = userData[1];
        UserEntity tempUser = service.findUser(username);

        if(tempUser == null || !(tempUser.getPassword().equals(password))){
            throw new MyExceptionUtil(HttpReturnServerStatus.CODE_401, "No User or incorrect password ");
        }
        LOGGER.info("Task Handler: user exists:" + username);
        return tempUser;
    }

}
