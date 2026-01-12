package org.example;

public class NegativeTopUpDemo {
    public static void main(String[] args) {
        double currentBalance = 25.00;
        double topUpAmount = -10.00;

        System.out.println("Current balance: " + String.format("%.2f", currentBalance) + " EUR");
        System.out.println("Requested top-up: " + String.format("%.2f", topUpAmount) + " EUR");

        if (topUpAmount <= 0) {
            System.out.println("Top-up rejected: amount must be positive");
            System.out.println("Balance unchanged: " + String.format("%.2f", currentBalance) + " EUR");
            return;
        }

        currentBalance += topUpAmount;
        System.out.println("Top-up accepted");
        System.out.println("New balance: " + String.format("%.2f", currentBalance) + " EUR");
    }
}
