package com.anas.data;

public class AdminAuth {

    private static final String USERNAME = "admin";

    // Password 1: Simple one for quick login
    private static final String PASS_REGULAR = "1234";

    // Password 2: Complex one (backup master key)
    private static final String PASS_MASTER = "Admin#2025@TechBites!";

    public static boolean validate(String user, String inputPass) {
        // 1. Check Username first
        if (!user.equals(USERNAME)) {
            return false;
        }

        // 2. Check if password matches EITHER Regular OR Master
        return inputPass.equals(PASS_REGULAR) || inputPass.equals(PASS_MASTER);
    }
}