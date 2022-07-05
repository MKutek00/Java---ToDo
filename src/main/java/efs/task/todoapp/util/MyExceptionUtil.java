package efs.task.todoapp.util;

import efs.task.todoapp.web.HttpReturnServerStatus;

public class MyExceptionUtil extends RuntimeException{
    HttpReturnServerStatus httpReturnServerStatus;

    public MyExceptionUtil(HttpReturnServerStatus httpReturnServerStatus, String text){
        super(text);
        this.httpReturnServerStatus = httpReturnServerStatus;

    }

    public HttpReturnServerStatus getHttpStatus() {
        return httpReturnServerStatus;
    }
}
