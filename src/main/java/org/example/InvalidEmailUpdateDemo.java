package org.example;

public class InvalidEmailUpdateDemo {
    public static void main(String[] args) {
        String oldEmail = "martin.keller@testmail.com";
        String newEmail = "invalid-email";

        System.out.println("Old email: " + oldEmail);
        System.out.println("Requested new email: " + newEmail);

        if (!isValidEmail(newEmail)) {
            System.out.println("Email update rejected: invalid format");
            return;
        }

        System.out.println("Email update accepted: " + newEmail);
    }

    private static boolean isValidEmail(String email) {
        if (email == null) return false;
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
}
