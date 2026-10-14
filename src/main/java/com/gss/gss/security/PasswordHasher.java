package com.gss.gss.security;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordHasher {

    private PasswordHasher() {
    }

    public static String hash(String password) {

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException(
                    "Le mot de passe ne peut pas être vide."
            );
        }

        return BCrypt.hashpw(
                password,
                BCrypt.gensalt(12)
        );
    }

    public static boolean verify(
            String password,
            String hashedPassword
    ) {

        if (password == null || hashedPassword == null) {
            return false;
        }

        try {

            return BCrypt.checkpw(
                    password,
                    hashedPassword
            );

        } catch (IllegalArgumentException e) {

            return false;
        }
    }
}