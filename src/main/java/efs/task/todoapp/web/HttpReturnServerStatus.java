package efs.task.todoapp.web;

public enum HttpReturnServerStatus {
    CODE_200(200),
    CODE_201(201),
    CODE_400(400),
    CODE_401(401),
    CODE_403(403),
    CODE_404(404),
    CODE_409(409),
    TEST(666);

    private final int errorCode;

    HttpReturnServerStatus(int errorCode){
        this.errorCode = errorCode;
    }

    public int getErrorCode(){
        return errorCode;
    }

}
