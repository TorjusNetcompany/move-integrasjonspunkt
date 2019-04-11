package no.difi.meldingsutveksling;

import no.difi.meldingsutveksling.domain.MeldingsUtvekslingRuntimeException;
import no.difi.meldingsutveksling.domain.Organisasjonsnummer;
import no.difi.meldingsutveksling.domain.sbdh.PartnerIdentification;
import no.difi.meldingsutveksling.domain.sbdh.Receiver;
import no.difi.meldingsutveksling.domain.sbdh.StandardBusinessDocumentHeader;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StandardBusinessDocumentHeaderTest {

    @Test(expected = MeldingsUtvekslingRuntimeException.class)
    public void testShouldFailOnWrongReceiverListsizeZero() {
        StandardBusinessDocumentHeader header = new StandardBusinessDocumentHeader();
        header.getReceiver();
        header.getReceiverOrganisationNumber();
    }

    @Test(expected = MeldingsUtvekslingRuntimeException.class)
    public void testShouldFailOnWrongReceiverListsizeOneOrMore() {
        StandardBusinessDocumentHeader header = new StandardBusinessDocumentHeader();
        header.getReceiver().add(new Receiver());
        header.getReceiver().add(new Receiver());
        header.getReceiverOrganisationNumber();
    }

    @Test(expected = MeldingsUtvekslingRuntimeException.class)
    public void testMissingIdentifierOnPartner() {
        StandardBusinessDocumentHeader header = new StandardBusinessDocumentHeader();
        header.getReceiver().add(new Receiver());
        header.getReceiverOrganisationNumber();
    }

    @Test
    public void positiveTest() {
        StandardBusinessDocumentHeader header = new StandardBusinessDocumentHeader();
        Receiver p = new Receiver();
        final PartnerIdentification value = new PartnerIdentification();
        value.setAuthority("authorotai");
        value.setValue("011076111111");
        p.setIdentifier(value);
        header.getReceiver().add(p);
        header.getReceiverOrganisationNumber();
    }

    @Test
    public void testBuildKvittering() {
        StandardBusinessDocumentHeader h = new StandardBusinessDocumentHeader.Builder()
                .from(Organisasjonsnummer.fromIso6523("123456789"))
                .to(Organisasjonsnummer.fromIso6523("123456789"))
                .relatedToJournalPostId("some journalpost")
                .relatedToConversationId("some conversation")
                .process(Process.LEGACY)
                .standard(Standard.LEGACY.getValue())
                .type(DocumentType.BESTEDU_KVITTERING)
                .build();
        assertEquals(Standard.LEGACY.getValue(), h.getDocumentIdentification().getStandard());
        assertEquals(DocumentType.BESTEDU_KVITTERING.getType(), h.getDocumentIdentification().getType());
        assertEquals("2.0", h.getDocumentIdentification().getTypeVersion());
    }

    @Test
    public void testBuildMelding() {
        StandardBusinessDocumentHeader h = new StandardBusinessDocumentHeader.Builder()
                .from(Organisasjonsnummer.fromIso6523("123456789"))
                .to(Organisasjonsnummer.fromIso6523("123456789"))
                .relatedToJournalPostId("some journalpost")
                .relatedToConversationId("some conversation")
                .process(Process.LEGACY)
                .standard(Standard.LEGACY.getValue())
                .type(DocumentType.BESTEDU_MELDING)
                .build();
        assertEquals(Standard.LEGACY.getValue(), h.getDocumentIdentification().getStandard());
        assertEquals(DocumentType.BESTEDU_MELDING.getType(), h.getDocumentIdentification().getType());
        assertEquals("2.0", h.getDocumentIdentification().getTypeVersion());
    }

    @Test(expected = MeldingsUtvekslingRuntimeException.class)
    public void testBuildWithoutType() {
        StandardBusinessDocumentHeader h = new StandardBusinessDocumentHeader.Builder()
                .from(Organisasjonsnummer.fromIso6523("123456789"))
                .to(Organisasjonsnummer.fromIso6523("123456789"))
                .relatedToJournalPostId("some journalpost")
                .relatedToConversationId("some conversation")
                .build();
    }

}