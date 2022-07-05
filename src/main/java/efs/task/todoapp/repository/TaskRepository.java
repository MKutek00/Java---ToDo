package efs.task.todoapp.repository;

import efs.task.todoapp.util.MyExceptionUtil;
import efs.task.todoapp.web.HttpReturnServerStatus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.logging.Logger;

public class TaskRepository implements Repository<UUID, TaskEntity> {
    private static final Logger LOGGER = Logger.getLogger(TaskRepository.class.getName());

    Map<UUID, TaskEntity> mapOfTasks = new TreeMap<>();

    @Override
    public UUID save(TaskEntity taskEntity) throws MyExceptionUtil{
        if(taskEntity == null){
            return null;
        }
        if (taskEntity.getDescription() == null || taskEntity.getDescription().isBlank()) {
            throw new MyExceptionUtil(HttpReturnServerStatus.CODE_400, "Description is Null");
        }

        UUID uuid = UUID.randomUUID();
        taskEntity.setUUID(uuid);
        mapOfTasks.put(uuid, taskEntity);
        return uuid;
    }

    @Override
    public TaskEntity query(UUID uuid) {
        return mapOfTasks.get(uuid);
    }

    @Override
    public List<TaskEntity> query(Predicate<TaskEntity> condition) {
        List<TaskEntity> resultList = new ArrayList<>();

        for(TaskEntity tempTask : mapOfTasks.values()){
            if(condition.test(tempTask)){
                resultList.add(tempTask);
            }
        }
        return resultList;
    }

    @Override
    public TaskEntity update(UUID uuid, TaskEntity updateTask) {
        TaskEntity tempTask = mapOfTasks.get(uuid);
        LOGGER.info("TaskRepository, update on task" + updateTask.taskToJson());
        String newDescription = updateTask.getDescription();
        String newDue = updateTask.getDue();

        if(newDescription != null && !newDescription.isBlank()){
            tempTask.setDescription(newDescription);
            tempTask.setDue(newDue);
        }
        return tempTask;
    }

    @Override
    public boolean delete(UUID uuid) {
        LOGGER.info("TaskRepository: Remove Task with UUID:" + uuid);
        return (mapOfTasks.remove(uuid) != null);
    }

}
