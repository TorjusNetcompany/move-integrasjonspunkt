package no.difi.meldingsutveksling.cucumber;

import lombok.Data;
import no.difi.meldingsutveksling.ServiceIdentifier;
import no.difi.meldingsutveksling.domain.sbdh.StandardBusinessDocument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public class Message {

    private StandardBusinessDocument sbd;
    private String body;
    private List<Attachment> attachments = new ArrayList<>();
    private String sender;
    private String receiver;
    private String conversationId;
    private String messageId;
    private ServiceIdentifier serviceIdentifier;

    Message attachment(Attachment attachment) {
        attachments.add(attachment);
        return this;
    }

    Message attachments(Collection<Attachment> in) {
        attachments.addAll(in);
        return this;
    }

    Attachment getFirstAttachment() {
        return attachments.get(0);
    }

    Attachment getAttachment(String filename) {
        return attachments.stream()
                .filter(p -> p.getFileName().equals(filename))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format("File not found for %s", filename)));
    }

}
