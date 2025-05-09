package com.bank.util;

import com.bank.model.Loan;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailUtil {
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String USERNAME = "youremail@gmail.com"; // Fixed email address
    private static final String PASSWORD = "abc def ghi jkl"; // Replace with your Google App Password

    public static void sendLoanApplicationEmail(String recipientEmail, String userName) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Loan Application Received - Document Verification Required");

            String emailContent = "Dear " + userName + ",\n\n" +
                    "Thank you for submitting your loan application. We have received your request and need to verify your details.\n\n"
                    +
                    "To proceed with your loan application, please send the following documents to this email address:\n"
                    +
                    "1. Proof of Identity (Aadhar Card/Passport)\n" +
                    "2. Proof of Address\n" +
                    "3. Income Proof (Salary Slips/Bank Statements)\n\n" +
                    "Once we receive and verify your documents, there will be a one to one meeting then we will process your loan application.\n\n" +
                    "Best regards,\n" +
                    "Bank Management Team";

            message.setText(emailContent);

            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            // You might want to log this error
        }
    }

    public static void sendLoanApprovalEmail(String recipientEmail, String userName, Loan loan) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Congratulations! Your Loan Has Been Approved");

            String emailContent = "Dear " + userName + ",\n\n" +
                    "We are pleased to inform you that your loan application has been approved!\n\n" +
                    "Loan Details:\n" +
                    "Loan ID: " + loan.getLoanId() + "\n" +
                    "Amount: $" + String.format("%.2f", loan.getLoanAmount()) + "\n" +
                    "Interest Rate: " + String.format("%.2f", loan.getInterestRate()) + "%\n" +
                    "Start Date: " + loan.getStartDate() + "\n" +
                    "End Date: " + loan.getEndDate() + "\n" +
                    "Status: " + loan.getStatus() + "\n\n" +
                    "The approved loan amount has been credited to your current account.\n" +
                    "Please ensure timely repayment to maintain a good credit score.\n\n" +
                    "If you have any questions, please contact our customer support.\n\n" +
                    "Best regards,\n" +
                    "Bank Management Team";

            message.setText(emailContent);

            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            // You might want to log this error
        }
    }

    public static void sendAccountApprovalEmail(String recipientEmail, String userName) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Account Approved - Welcome to Our Bank");

            String emailContent = "Dear " + userName + ",\n\n" +
                    "We are pleased to inform you that your account has been approved!\n\n" +
                    "You can now log in to your account and access all our banking services.\n\n" +
                    "If you have any questions or need assistance, please don't hesitate to contact our customer support.\n\n" +
                    "Best regards,\n" +
                    "Bank Management Team";

            message.setText(emailContent);

            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            // You might want to log this error
        }
    }
}
