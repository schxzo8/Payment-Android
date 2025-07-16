package com.paymentapp.android.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private String transactionId;
    private String merchantId;
    private String merchantName;
    private BigDecimal amount;
    private String maskedCardNumber;
    private String cardExpiryDate;
    private LocalDateTime timestamp;
    private String status;


}