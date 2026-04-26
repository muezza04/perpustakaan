package com.nuzurwan.perpustakaan.scheduler;

import com.nuzurwan.perpustakaan.service.LoanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoanScheduler {

    private final LoanService loanService;

    /**
     * Berjalan otomatis setiap hari pada jam 00:00 (Tengah Malam).
     * Cron expression: "detik menit jam hari bulan hari-dalam-minggu"
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void runOverdueCheck() {
        log.info("Starting automated check for overdue loans...");
        try {
            loanService.updateOverdueStatus();
            log.info("Finished automated check for overdue loans.");
        } catch (Exception e) {
            log.error("Failed to run overdue check: {}", e.getMessage());
        }
    }
}