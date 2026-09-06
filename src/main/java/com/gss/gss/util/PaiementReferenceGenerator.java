package com.gss.gss.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public final class PaiementReferenceGenerator {

    private PaiementReferenceGenerator() {
    }

    public static String generate() {

        String date = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        String unique = UUID.randomUUID()
                .toString()
                .substring(0, 6)
                .toUpperCase();

        return "PAY-" + date + "-" + unique;
    }
}