package com.paymentapp.android.model;

public class PaymentResponse {
    private String transactionId;
    private String status;
    private String message;
    private double amount;
    private boolean success;  // Add this field

    public PaymentResponse() {}

    public PaymentResponse(boolean success, String message, String transactionId, double amount, String status) {
        this.success = success;
        this.message = message;
        this.transactionId = transactionId;
        this.amount = amount;
        this.status = status;
    }

    // Existing getters and setters
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    // NEW: Add these methods
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isSuccessful() {
        return success && ("success".equalsIgnoreCase(status) ||
                "completed".equalsIgnoreCase(status) ||
                "approved".equalsIgnoreCase(status));
    }

    public boolean isFailed() {
        return !success || "failed".equalsIgnoreCase(status) ||
                "declined".equalsIgnoreCase(status) || "rejected".equalsIgnoreCase(status);
    }

    public boolean isPending() {
        return "pending".equalsIgnoreCase(status) || "processing".equalsIgnoreCase(status);
    }
}