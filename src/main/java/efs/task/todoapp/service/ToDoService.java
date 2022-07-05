package efs.task.todoapp.service;

import efs.task.todoapp.repository.TaskRepository;
import efs.task.todoapp.repository.TaskEntity;
import efs.task.todoapp.repository.UserRepository;
import efs.task.todoapp.repository.UserEntity;
import efs.task.todoapp.web.HttpReturnServerStatus;
import efs.task.todoapp.util.MyExceptionUtil;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;


public class ToDoService {
    private static final Logger LOGGER = Logger.getLogger(ToDoService.class.getName());


    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public ToDoService(UserRepository userRepository, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    public void addUser(UserEntity userToAdd) throws MyExceptionUtil {
        String userUUID = userRepository.save(userToAdd);

        if(userUUID == null){
            throw new MyExceptionUtil(HttpReturnServerStatus.CODE_409, "User name exists");
        }
        LOGGER.info("AddUser done successfully" + userToAdd.getUsername());
    }

    public UserEntity findUser(String username){
        return userRepository.query(username);
    }

    public void addTask(TaskEntity taskEntity, String authorUsername) throws MyExceptionUtil {
        if (taskEntity.getDue() != null && !taskEntity.getDue().isBlank()) {
            try {
                new SimpleDateFormat("dd-MM-yyyy").parse(taskEntity.getDue());
            }
            catch (Exception e) {
                throw new MyExceptionUtil(HttpReturnServerStatus.CODE_400, "Niepoprawny format daty dla zadania!");
            }
        }

        taskEntity.setUsername(authorUsername);
        UUID taskUUID = taskRepository.save(taskEntity);

        LOGGER.info("AddTask done successfully" + taskUUID.toString());

    }

    public TaskEntity findTask(UUID id){
        return taskRepository.query(id);
    }

    public List<TaskEntity> findTaskOfUser(String username){
        return taskRepository.query(t -> t.getUsername().equals(username));
    }

    public TaskEntity updateTask(UUID id, TaskEntity updatedTask){
        return taskRepository.update(id, updatedTask);
    }

    public void removeTask(UUID id){
        taskRepository.delete(id);
    }
}
