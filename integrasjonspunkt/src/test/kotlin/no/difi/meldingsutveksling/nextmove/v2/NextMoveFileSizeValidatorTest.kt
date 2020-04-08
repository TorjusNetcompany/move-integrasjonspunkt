package no.difi.meldingsutveksling.nextmove.v2

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import no.difi.meldingsutveksling.ServiceIdentifier
import no.difi.meldingsutveksling.config.IntegrasjonspunktProperties
import no.difi.meldingsutveksling.exceptions.MaxFileSizeExceededException
import no.difi.meldingsutveksling.nextmove.BusinessMessageFile
import no.difi.meldingsutveksling.nextmove.NextMoveOutMessage
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.springframework.util.unit.DataSize
import javax.servlet.http.HttpServletRequest

class NextMoveFileSizeValidatorTest {

    @MockK
    lateinit var props: IntegrasjonspunktProperties

    lateinit var validator: NextMoveFileSizeValidator
    private val msg = mockk<NextMoveOutMessage>()
    private val req = mockk<HttpServletRequest>()
    val file = NextMoveUploadedFile("text/html", "attachment; filename=\"test.txt\"", "title", req)

    @Before
    fun before() {
        MockKAnnotations.init(this)
        every { props.dpo.uploadSizeLimit } returns DataSize.parse("10MB")
        validator = NextMoveFileSizeValidator(props)

        every { msg.serviceIdentifier } returns ServiceIdentifier.DPO
        every { msg.files } returns emptySet()
    }

    @Test
    fun `test upload is within limit`() {
        every { req.contentLengthLong } returns DataSize.parse("5MB").toBytes()
        validator.validate(msg, file)
    }

    @Test(expected = MaxFileSizeExceededException::class)
    fun `test upload exceeds limit size`() {
        every { req.contentLengthLong } returns DataSize.parse("100MB").toBytes()
        validator.validate(msg, file)
    }

    @Test(expected = MaxFileSizeExceededException::class)
    fun `test multiple uploads exceed limit size`() {
        val existingFile = mockk<BusinessMessageFile>()
        every { existingFile.size } returns DataSize.parse("6MB").toBytes()
        every { msg.files } returns setOf(existingFile)
        every { req.contentLengthLong } returns DataSize.parse("5MB").toBytes()
        validator.validate(msg, file)
    }

}