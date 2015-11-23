package no.difi.meldingsutveksling.scheduler;

import no.difi.meldingsutveksling.config.IntegrasjonspunktConfig;
import no.difi.meldingsutveksling.noarkexchange.IntegrasjonspunktImpl;
import no.difi.meldingsutveksling.noarkexchange.schema.PutMessageRequestType;
import no.difi.meldingsutveksling.queue.domain.Queue;
import no.difi.meldingsutveksling.queue.domain.Status;
import no.difi.meldingsutveksling.queue.service.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class QueueScheduler {
    public static final String FIRE_EVERY_1_MINUTE = "0 0/1 * * * ?";
    private final QueueService queueService;
    private final IntegrasjonspunktImpl integrasjonspunkt;

    private IntegrasjonspunktConfig integrasjonspunktConfig;

    @Autowired
    public QueueScheduler(QueueService queueService, IntegrasjonspunktImpl integrasjonspunkt,
                          IntegrasjonspunktConfig integrasjonspunktConfig) {
        this.queueService = queueService;
        this.integrasjonspunkt = integrasjonspunkt;
        this.integrasjonspunktConfig = integrasjonspunktConfig;
    }

    @Scheduled(cron = FIRE_EVERY_1_MINUTE)
    public void sendMessage() {
        if (integrasjonspunktConfig.isQueueEnabled()) {
            System.out.println("************************************************");
            System.out.println("Attempting to sent messages that have status new");
            System.out.println("************************************************");

            Queue next = queueService.getNext(Status.NEW);
            if (next != null) {
                boolean success = integrasjonspunkt.sendMessage((PutMessageRequestType) queueService.getMessage(next.getUnique()));
                applyResultToQueue(next.getUnique(), success);
            }
        }
    }

    @Scheduled(cron = FIRE_EVERY_1_MINUTE)
    public void retryMessages() {
        System.out.println("***************************************************");
        System.out.println("Attempting to sent messages that have status failed");
        System.out.println("***************************************************");
        if (integrasjonspunktConfig.isQueueEnabled()) {
            Queue next = queueService.getNext(Status.FAILED);
            if (next != null) {
                boolean success = integrasjonspunkt.sendMessage((PutMessageRequestType) queueService.getMessage(next.getUnique()));
                applyResultToQueue(next.getUnique(), success);
            }
        }
    }

    private void applyResultToQueue(String unique, boolean result) {
        if (result) {
            queueService.success(unique);
        }
        else {
            queueService.fail(unique);
        }
    }
}
