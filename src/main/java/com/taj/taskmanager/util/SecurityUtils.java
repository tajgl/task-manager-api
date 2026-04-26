package com.taj.taskmanager.util;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}