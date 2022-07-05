package efs.task.todoapp.repository;

import efs.task.todoapp.util.MyExceptionUtil;
import efs.task.todoapp.web.HttpReturnServerStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.logging.Logger;

public class UserRepository implements Repository<String, UserEntity> {
    private static final Logger LOGGER = Logger.getLogger(UserRepository.class.getName());

    private  final Map<String, UserEntity> mapOfUsers = new HashMap<>();

    @Override
    public String save(UserEntity userEntity) throws MyExceptionUtil {
        if(userEntity == null){
            return null;
        }
        String name = userEntity.getUsername();
        String password = userEntity.getPassword();
        if(name == null || name.isBlank() || password == null || password.isBlank()){
            throw new MyExceptionUtil(HttpReturnServerStatus.CODE_400, "Name or Password is Null");
        }

        String key = userEntity.getUsername();
        if (mapOfUsers.containsKey(key)) {
            return null;
        }
        mapOfUsers.put(key, userEntity);
        return key;
    }

    @Override
    public UserEntity query(String s) {
        return mapOfUsers.get(s);
    }

    @Override
    public List<UserEntity> query(Predicate<UserEntity> condition) {
        List<UserEntity> resultList = new ArrayList<>();

        for(UserEntity testUser : mapOfUsers.values()){
            if(condition.test(testUser)){
                resultList.add(testUser);
            }
        }
        return resultList;
    }

    @Override
    public UserEntity update(String s, UserEntity userUpdate) {
        UserEntity tempUser = mapOfUsers.get(s);

        String newUsername = userUpdate.getUsername();
        String newPassword = userUpdate.getPassword();

        if(newUsername != null && !newUsername.isBlank() && newPassword != null && !newPassword.isBlank()){
            tempUser.setUsername(newUsername);
            tempUser.setPassword(newPassword);
        }

        return tempUser;
    }

    @Override
    public boolean delete(String s) {
        return (mapOfUsers.remove(s) != null);
    }

}
