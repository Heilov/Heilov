package com.heilov.heilov.DAO;

import com.heilov.heilov.Model.User;

import java.util.List;

public interface UserCallback {
    void onCallback(User u);

    void onCallback(List<User> userList);
}
