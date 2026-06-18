package com.example.picture.context;

import com.example.picture.dto.UserDTO;

public class UserContext {

    private static final ThreadLocal<UserDTO> currentUser = new ThreadLocal<>();

    public static void setCurrentUser(UserDTO user) {
        currentUser.set(user);
    }

    public static UserDTO getCurrentUser() {
        return currentUser.get();
    }

    public static Long getCurrentUserId() {
        UserDTO user = currentUser.get();
        return user != null ? user.getId() : null;
    }

    public static void clear() {
        currentUser.remove();
    }

    public static boolean isLoggedIn() {
        return currentUser.get() != null;
    }
}
