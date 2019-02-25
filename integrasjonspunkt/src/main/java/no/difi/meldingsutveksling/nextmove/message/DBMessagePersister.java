package no.difi.meldingsutveksling.nextmove.message;

import lombok.extern.slf4j.Slf4j;
import no.difi.meldingsutveksling.config.IntegrasjonspunktProperties;
import no.difi.meldingsutveksling.nextmove.ConversationResource;
import org.apache.commons.io.IOUtils;
import org.hibernate.LobHelper;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Optional;

import static no.difi.meldingsutveksling.NextMoveConsts.ASIC_FILE;

@Slf4j
@Component
@ConditionalOnProperty(name = "difi.move.nextmove.useDbPersistence", havingValue = "true")
public class DBMessagePersister implements MessagePersister {

    private NextMoveMessageEntryRepository repo;
    private IntegrasjonspunktProperties props;
    private EntityManager em;

    @Autowired
    public DBMessagePersister(NextMoveMessageEntryRepository repo, IntegrasjonspunktProperties props, EntityManager em) {
        this.repo = repo;
        this.props = props;
        this.em = em;
    }

    @Override
    @Transactional
    public void write(ConversationResource cr, String filename, byte[] message) throws IOException {
        LobHelper lobHelper = em.unwrap(Session.class).getLobHelper();
        Blob contentBlob = lobHelper.createBlob(message);
        if (props.getNextmove().getApplyZipHeaderPatch() && ASIC_FILE.equals(filename)) {
            BugFix610.applyPatch(message, cr.getConversationId());
        }

        NextMoveMessageEntry entry = NextMoveMessageEntry.of(cr.getConversationId(), filename, contentBlob, message.length);
        repo.save(entry);
    }

    @Override
    @Transactional
    public void writeStream(ConversationResource cr, String filename, InputStream stream, long size) throws IOException {
        LobHelper lobHelper = em.unwrap(Session.class).getLobHelper();
        Blob contentBlob = lobHelper.createBlob(stream, stream.available());

        NextMoveMessageEntry entry = NextMoveMessageEntry.of(cr.getConversationId(), filename, contentBlob, size);
        repo.save(entry);

    }

    @Override
    public byte[] read(ConversationResource cr, String filename) throws IOException {
        Optional<NextMoveMessageEntry> entry = repo.findByConversationIdAndFilename(cr.getConversationId(), filename);
        if (entry.isPresent()) {
            try {
                return IOUtils.toByteArray(entry.get().getContent().getBinaryStream());
            } catch (SQLException e) {
                throw new IOException("Error reading data stream from database", e);

            }

        }
        throw new IOException(String.format("File \'%s\' for conversation with id=%s not found in repository", filename, cr.getConversationId()));
    }

    @Override
    public FileEntryStream readStream(ConversationResource cr, String filename) throws PersistenceException {
        Optional<NextMoveMessageEntry> entry = repo.findByConversationIdAndFilename(cr.getConversationId(), filename);
        if (entry.isPresent()) {
            try {
                return FileEntryStream.of(entry.get().getContent().getBinaryStream(), entry.get().getSize());
            } catch (SQLException e) {
                throw new PersistenceException("Error reading data stream from database", e);
            }
        }
        throw new PersistenceException(String.format("Entry for conversationId=%s, filename=%s not found in database", cr.getConversationId(), filename));
    }

    @Override
    @Transactional
    public void delete(ConversationResource cr) throws IOException {
        repo.deleteByConversationId(cr.getConversationId());
    }
}
