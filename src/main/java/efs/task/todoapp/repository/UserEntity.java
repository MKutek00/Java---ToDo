package efs.task.todoapp.repository;


public class UserEntity {
    private String username;
    private String password;

    public UserEntity() {}

    public String getUsername(){
        return username;
    }
    public void setUsername(String newUsername){ this.username = newUsername; }

    public String getPassword(){
        return password;
    }
    public void setPassword(String newPassword){ this.password = newPassword; }
}
