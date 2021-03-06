package no.difi.meldingsutveksling.noarkexchange.altinn;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Optional;

/**
 * MessagePolling periodically checks Altinn Formidlingstjeneste for new messages. If new messages are discovered they are
 * downloaded forwarded to the Archive system.
 */
@Slf4j
@RequiredArgsConstructor
public class MessagePolling {

    private final ObjectProvider<DpePolling> dpePolling;
    private final ObjectProvider<DpfPolling> dpfPolling;
    private final ObjectProvider<DpoPolling> dpoPolling;

    @Scheduled(fixedDelayString = "${difi.move.nextmove.serviceBus.pollingrate}")
    public void checkForNewEinnsynMessages() {
        Optional.ofNullable(dpePolling.getIfAvailable()).ifPresent(DpePolling::poll);
    }

    @Scheduled(fixedDelayString = "${difi.move.fiks.pollingrate}")
    public void checkForFiksMessages() {
        Optional.ofNullable(dpfPolling.getIfAvailable()).ifPresent(DpfPolling::poll);
    }

    @Scheduled(fixedDelay = 15000)
    public void checkForNewAltinnMessages() {
        Optional.ofNullable(dpoPolling.getIfAvailable()).ifPresent(DpoPolling::poll);
    }
}
