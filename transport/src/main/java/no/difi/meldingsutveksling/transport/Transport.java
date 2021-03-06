package no.difi.meldingsutveksling.transport;

import no.difi.meldingsutveksling.domain.sbdh.StandardBusinessDocument;
import org.springframework.context.ApplicationContext;

import java.io.InputStream;

/**
 * Defines a transport. The responsibility of a transport is to receive an SBD ducument and transfer it over some transportation
 * mechanism; oxalis, Altinn, Dropbox or whatever.
 * <p/>
 * See individual modules for implementations of transports.
 *
 * @author Glenn bech
 * @see TransportFactory
 */
public interface Transport {

    /**
     * @param sbd An sbd with a payload consisting of an CMS encrypted ASIC package
     */
    void send(ApplicationContext context, StandardBusinessDocument sbd);

    /**
     * @param sbd An sbd with a payload consisting of metadata only
     * @param is InputStream pointing to the encrypted ASiC package
     */
    void send(ApplicationContext context, StandardBusinessDocument sbd, InputStream is);
}
