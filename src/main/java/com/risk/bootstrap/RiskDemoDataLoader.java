package com.risk.bootstrap;

import com.risk.entity.Risk;
import com.risk.repository.RiskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Seeds the risk register with demo data when the database is empty (typical first startup).
 */
@Component
@Profile("!test")
@Order(Ordered.LOWEST_PRECEDENCE)
@ConditionalOnProperty(name = "app.demo.seed-risks", havingValue = "true", matchIfMissing = true)
public class RiskDemoDataLoader implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(RiskDemoDataLoader.class);

    /** Status values aligned with queries (e.g. overdue excludes MITIGATED/CLOSED). */
    private static final String[] STATUSES =
            {"OPEN", "IN_PROGRESS", "MITIGATED", "CLOSED", "DEFERRED"};

    private static final String[] CATEGORIES =
            {"Operational", "Technical", "Financial", "Compliance", "Security", "Strategic"};

    private static final String[] TITLES = {
            "Third-party SaaS outage could expose regulated customer data",
            "Critical CVE on internet-facing load balancer fleet",
            "Workload concentrated in a single cloud availability zone",
            "GDPR retention gaps in legacy CRM nightly exports",
            "Payroll batch chained to end-of-life mainframe scheduler",
            "Excessive dormant privileged accounts after reorganisation",
            "Ransomware recovery runbooks never exercised end-to-end",
            "Core HRIS vendor overdue on SOC 2 Type II renewal",
            "Backup encryption inconsistent at warm standby site",
            "Undetected drift on production fraud scoring model",
            "Treasury exposure concentrated with single tier-1 counterparty",
            "Interest-rate shock assumptions not refreshed since baseline",
            "Wire approvals may bypass maker-checker during core outage",
            "Pension calculation spreadsheet lacks formal controls",
            "Market-abuse surveillance tuning backlog growing",
            "Missing HIPAA BA agreements for two acquired clinics",
            "Clinical trial consent versioning inconsistent across sites",
            "Workplace safety incident reporting latency exceeds policy SLA",
            "DPIA not refreshed before GenAI customer-support pilot",
            "Wildcard TLS certificate expires before renewal workflow completes",
            "Single SME owns treasury payment file format end-to-end",
            "No named successor for CISO covering statutory duties",
            "Board cyber briefing pack lags major incidents by two months",
            "Unsupported product line still exposed after portfolio pivot",
            "IAM consolidation delayed post-merger — duplicate identities persist",
            "Targeted phishing campaign against finance leadership",
            "Legacy contractor cohort exempt from MFA on remote access",
            "Deploy keys historically committed to repository history",
            "East-west container traffic lacks unified log correlation",
            "Business continuity plan assumes office return within 48 hours"
    };

    private static final String[] DESCRIPTIONS = {
            "Primary CRM relies on vendor with recurring regional outages; no active failover playbook.",
            "CVE bulletin rated CVSS 9+; patching window conflicts with peak trading freeze.",
            "All production Kubernetes workloads pinned to one region; no automated failover tested.",
            "Exports retain fields beyond lawful retention; deletion jobs partially failing silently.",
            "Batch depends on unsupported COBOL job chain; vendor maintenance ends next quarter.",
            "Quarterly access reviews stalled; 200+ admin-equivalent accounts never deactivated.",
            "Last tabletop was slide-only; restore-from-immutable backups not timed.",
            "Contract requires annual report; procurement/legal reviews stuck in queue.",
            "Warm-site tapes/restores use weaker cipher suites than production mandate.",
            "Monitoring compares weekly aggregates only; subtle score shifts ignored.",
            "Stress liquidity ladder shows breach if counterparty downgrades two notches.",
            "Parallel-rate scenarios omit recent macro shocks used by regulators elsewhere.",
            "Emergency runbook allows single-sign-off when workflow engine offline.",
            "Macros combine external feeds without checksum validation.",
            "Alert backlog > SLA for tuning analyst capacity.",
            "BA templates updated centrally but regional onboarding skipped scans.",
            "Sites ship consent PDF revisions asynchronously to sponsors.",
            "Plant supervisors batch-enter incidents weekly vs 24h regulatory expectation.",
            "Pilot processes sensitive transcripts before DPIA sign-off.",
            "Renewal ticket opened late; dependent microservices share same SAN.",
            "Documentation sparse; cross-training deferred twice.",
            "Interim coverage assigns junior analyst without board-approved mandate.",
            "Incident timelines merged manually from email threads.",
            "SKU sunset stopped marketing but APIs remain public.",
            "Duplicate LDAP groups preserved for 'temporary' integrations.",
            "Campaign mimics approved OAuth prompts; training completion under 40%.",
            "VPN profile exceptions renewed yearly without risk acceptance.",
            "Historical commits contain PEM blocks; rotation incomplete.",
            "Network policy logs land in separate SIEM partition with 7-day retention.",
            "Scenario predates hybrid-remote operating model adopted last year."
    };

    private final RiskRepository riskRepository;

    public RiskDemoDataLoader(RiskRepository riskRepository) {
        this.riskRepository = riskRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (riskRepository.count() > 0) {
            log.debug("Skipping demo risk seed: {} risk rows already present.", riskRepository.count());
            return;
        }

        LocalDate today = LocalDate.now();
        List<Risk> batch = new ArrayList<>(30);
        for (int i = 0; i < 30; i++) {
            Risk r = new Risk();
            r.setTitle(TITLES[i]);
            r.setDescription(DESCRIPTIONS[i]);
            r.setCategory(CATEGORIES[i % CATEGORIES.length]);
            // Indices 0–24: every pair (likelihood, impact) in 1–5 × 1–5. Last five repeat notable extremes/mid.
            int likelihood;
            int impact;
            if (i < 25) {
                likelihood = 1 + (i % 5);
                impact = 1 + ((i / 5) % 5);
            } else {
                int[][] extras = {{5, 5}, {1, 1}, {3, 3}, {2, 4}, {4, 2}};
                likelihood = extras[i - 25][0];
                impact = extras[i - 25][1];
            }
            r.setLikelihood(likelihood);
            r.setImpact(impact);
            r.setStatus(STATUSES[i % STATUSES.length]);
            r.setDueDate(demoDueDate(today, i, r.getStatus()));
            r.setAiDescription("Demo seed — residual rating L" + likelihood + "×I" + impact + ".");
            batch.add(r);
        }

        riskRepository.saveAll(batch);
        log.info("Demo data: inserted {} risk records (statuses: {}; scores L/I in 1–5).",
                batch.size(), String.join(", ", STATUSES));
    }

    /** Mix overdue, due-soon, and future dates; closed/mitigated skew toward past. */
    private static LocalDate demoDueDate(LocalDate today, int index, String status) {
        int bucket = index % 7;
        LocalDate base = switch (bucket) {
            case 0 -> today.minusDays(45);
            case 1 -> today.minusDays(12);
            case 2 -> today.plusDays(5);
            case 3 -> today.plusDays(35);
            case 4 -> today.plusDays(120);
            case 5 -> today.minusDays(3);
            default -> today.plusDays(1);
        };
        if ("MITIGATED".equals(status) || "CLOSED".equals(status)) {
            return today.minusDays(20 + (index % 40));
        }
        return base;
    }
}
