package com.risk.scheduler;

import com.risk.entity.Risk;
import com.risk.service.EmailService;
import com.risk.service.RiskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RiskReminderScheduler {

    @Autowired
    private RiskService riskService;

    @Autowired
    private EmailService emailService;

    // ✅ Runs every day at 8:00 AM — sends overdue alerts
    @Scheduled(cron = "0 0 8 * * ?")
    public void sendOverdueReminders() {
        System.out.println("⏰ Running overdue reminder job...");
        List<Risk> overdueRisks = riskService.getOverdueRisks();
        overdueRisks.forEach(risk ->
            emailService.sendOverdueAlert(
                risk.getTitle() != null ? risk.getTitle() : "Unknown",
                risk.getTitle()
            )
        );
        System.out.println("✅ Overdue reminder job done. Processed: " + overdueRisks.size());
    }

    // ✅ Runs every day at 9:00 AM — warns about risks due in 7 days
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendUpcomingDeadlineAlerts() {
        System.out.println("⏰ Running 7-day deadline alert job...");
        List<Risk> upcomingRisks = riskService.getRisksDueWithinDays(7);
        upcomingRisks.forEach(risk ->
            emailService.sendDeadlineAlert(
                risk.getTitle() != null ? risk.getTitle() : "Unknown",
                risk.getTitle(),
                7
            )
        );
        System.out.println("✅ Deadline alert job done. Processed: " + upcomingRisks.size());
    }

    // ✅ Runs every Monday at 7:00 AM — weekly summary
    @Scheduled(cron = "0 0 7 * * MON")
    public void sendWeeklySummary() {
        System.out.println("⏰ Running weekly summary job...");
        long openRisks = riskService.countOpenRisks();
        long overdueRisks = riskService.getOverdueRisks().size();
        emailService.sendWeeklySummary(openRisks, overdueRisks);
        System.out.println("✅ Weekly summary job done.");
    }

    // ✅ Test job — runs every 2 minutes so you can SEE it working
    @Scheduled(fixedRate = 120000)
    public void testScheduler() {
        System.out.println("🔔 Scheduler is alive! Overdue risks: "
            + riskService.getOverdueRisks().size());
    }
}