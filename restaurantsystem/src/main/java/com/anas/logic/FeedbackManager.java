
package com.anas.logic;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FeedbackManager {
    private static final String FILE_NAME = "reviews.txt";

    public static void saveFeedback(String name, String message) {
        try (FileWriter fw = new FileWriter(FILE_NAME, true);
             PrintWriter pw = new PrintWriter(fw)) {
             
            // Get current time
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
            String time = dtf.format(LocalDateTime.now());

            // We replace Enter (\n) with a space.
            String safeMessage = message.replace("\n", " ");

            // SAVE FORMAT: Time | Name | Message
            pw.println(time + " | " + name + " | " + safeMessage);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}