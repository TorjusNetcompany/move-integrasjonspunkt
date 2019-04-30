package no.difi.meldingsutveksling.cucumber;

import cucumber.api.java.en.Then;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import no.altinn.services.serviceengine.correspondence._2009._10.InsertCorrespondenceV2;
import no.altinn.services.serviceengine.reporteeelementlist._2010._10.BinaryAttachmentV2;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class CorrespondenceAgencyClientSteps {

    private final XMLMarshaller xmlMarshaller;
    private final Holder<Message> messageSentHolder;
    private final Holder<List<String>> webServicePayloadHolder;

    @Then("^the CorrespondenceAgencyClient is called with the following payload:$")
    public void theCorrespondenceAgencyClientIsCalledWithTheFollowingPayload(String expectedPayload) {
        List<String> payloads = webServicePayloadHolder.get();
        String actualPayload = payloads.get(0);
        assertThat(hideData(actualPayload)).isXmlEqualTo(expectedPayload);

        InsertCorrespondenceV2 in = xmlMarshaller.unmarshall(actualPayload, InsertCorrespondenceV2.class);

        List<Attachment> attachments = in.getCorrespondence()
                .getContent().getValue()
                .getAttachments().getValue()
                .getBinaryAttachments().getValue()
                .getBinaryAttachmentV2()
                .stream()
                .map(p -> new Attachment(getInpuStream(p))
                        .setFileName(p.getFileName().getValue())
                ).collect(Collectors.toList());

        messageSentHolder.set(new Message()
                .attachments(attachments));
    }

    private String hideData(String s) {
        return s.replaceAll("<altinn11:Data>[^<]*</altinn11:Data>", "<altinn11:Data></altinn11:Data>");
    }

    @SneakyThrows
    private InputStream getInpuStream(BinaryAttachmentV2 p) {
        return p.getData().getValue().getInputStream();
    }
}
