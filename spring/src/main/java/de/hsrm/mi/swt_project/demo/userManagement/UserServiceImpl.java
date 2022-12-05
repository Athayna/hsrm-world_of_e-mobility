package de.hsrm.mi.swt_project.demo.userManagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of a UserService. Handles all actions on the List of users
 * 
 * @author Tom Gouthier
 */
@Service
public class UserServiceImpl implements UserService {

    UserList userList;

    @Autowired
    public UserServiceImpl(UserList userList) {
        this.userList = userList;
    }

    @Override
    public String addUser(String username) throws UserNotUniqueException, UsernameTooShortException {

        if (username.length() < 3) {
            throw new UsernameTooShortException("Username not long enough. Has to be 3 or above letters.");
        } else if (userList.getUserList().contains(username)) {
            throw new UserNotUniqueException("User is not unique");
        } else {
            userList.getUserList().add(username);
            return username;
        }

    }

    @Override
    public void removeUser(String username) {

        if (userList.getUserList().contains(username)) {
            userList.getUserList().remove(username);
        }
    }
}
