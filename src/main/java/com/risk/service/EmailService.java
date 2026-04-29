package com.risk.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    // ✅ Send overdue alert email
    public void sendOverdueAlert(String owner, String title) {
        // In real app this uses JavaMailSender
        // For now we log it — email setup needs SMTP credentials
        System.out.println("📧 OVERDUE ALERT: Risk '" + title + "' is overdue! Owner: " + owner);
    }

    // ✅ Send deadline alert email
    public void sendDeadlineAlert(String owner, String title, long daysLeft) {
        System.out.println("📧 DEADLINE ALERT: Risk '" + title + "' is due in " + daysLeft + " days! Owner: " + owner);
    }

    // ✅ Send weekly summary email
    public void sendWeeklySummary(long totalOpen, long totalOverdue) {
        System.out.println("📧 WEEKLY SUMMARY: Open risks: " + totalOpen + " | Overdue risks: " + totalOverdue);
    }
}