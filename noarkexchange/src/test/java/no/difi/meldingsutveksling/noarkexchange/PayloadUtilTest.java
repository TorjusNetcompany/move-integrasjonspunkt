package no.difi.meldingsutveksling.noarkexchange;

import net.logstash.logback.encoder.org.apache.commons.lang.StringEscapeUtils;
import no.difi.meldingsutveksling.noarkexchange.schema.AppReceiptType;
import no.difi.meldingsutveksling.noarkexchange.schema.PutMessageRequestType;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import static org.junit.Assert.*;

public class PayloadUtilTest {

    private TestData<PutMessageRequestType> testData;

    @Before
    public void setup() throws JAXBException {
        testData = new TestData<>(PutMessageRequestType.class);
    }

    @Test
    public void appReceiptMedFeilmeldingIsNotOkType() throws JAXBException, XMLStreamException {
        PutMessageRequestType value = testData.loadFromClasspath("p360/PutMessageAppReceiptFeilmelding.xml");

        final AppReceiptType appReceiptType = PayloadUtilKt.getAppReceiptType(value.getPayload());
        assertFalse(appReceiptType.getType().equals("OK"));
    }

    @Test
    public void getAppreceiptFromP360() throws JAXBException, XMLStreamException {
        PutMessageRequestType value = testData.loadFromClasspath("p360/PutMessageAppReceiptProblem.xml");

        String payload = StringEscapeUtils.unescapeHtml((String) value.getPayload());

        assertNotNull(PayloadUtilKt.getAppReceiptType(payload));
    }

    @Test
    public void isAppReceiptPutMessageFromEphorte() throws Exception {
        PutMessageRequestType value = testData.loadFromClasspath("ephorte/PutMessageMessage.xml");

        assertFalse(PayloadUtilKt.isAppReceipt(value.getPayload()));
    }

    @Test
    public void isAppReceiptAppreceiptFrom360() throws JAXBException, XMLStreamException {
        final PutMessageRequestType putMessageRequestType = testData.loadFromClasspath("p360/OKPutMessageAppReceipt.xml");

        assertTrue(PayloadUtilKt.isAppReceipt(putMessageRequestType.getPayload()));
    }

    @Test
    public void getOKAppReceiptTypeFromP360() throws JAXBException, XMLStreamException {
        final PutMessageRequestType putMessageRequestType = testData.loadFromClasspath("p360/OKPutMessageAppReceipt.xml");

        final AppReceiptType appReceiptType = PayloadUtilKt.getAppReceiptType(putMessageRequestType.getPayload());
        assertNotNull(appReceiptType);
        assertTrue(appReceiptType.getType().equals("OK"));
    }

    @Test
    public void isAppReceiptAppreceiptFromEphorte() throws JAXBException, XMLStreamException {
        final PutMessageRequestType putMessageRequestType = testData.loadFromClasspath("ephorte/PutMessageAppReceipt.xml");

        assertTrue(PayloadUtilKt.isAppReceipt(putMessageRequestType.getPayload()));
    }

    @Test
    public void isEmptyPayloadFromEphorte() throws JAXBException, XMLStreamException {
        final PutMessageRequestType putMessageRequestType = testData.loadFromClasspath("ephorte/PutMessageEmptyPayload.xml");

        assertTrue(PayloadUtilKt.isEmpty(putMessageRequestType.getPayload()));
    }
}