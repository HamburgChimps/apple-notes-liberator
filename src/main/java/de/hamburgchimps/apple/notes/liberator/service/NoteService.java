package de.hamburgchimps.apple.notes.liberator.service;

import com.ciofecaforensics.Notestore.NoteStoreProto;
import de.hamburgchimps.apple.notes.liberator.entity.Note;

import javax.enterprise.context.ApplicationScoped;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

import static java.util.zip.GZIPInputStream.GZIP_MAGIC;

@ApplicationScoped
public class NoteService {
    public List<Note> getAllNotes() {
        return Note.listAll();
    }

    public Optional<NoteStoreProto> parseZData(byte[] zData) throws IOException {
        if (zData == null) {
            return Optional.empty();
        }

        byte[] decompressed = (isCompressed(zData)) ? decompress(zData) : zData;

        return Optional.of(NoteStoreProto.parseFrom(decompressed));
    }

    private boolean isCompressed(byte[] data) {
        return data[0] == (byte) GZIP_MAGIC && data[1] == (byte) (GZIP_MAGIC >> 8);
    }

    private byte[] decompress(byte[] data) throws IOException {
        var inputBytes = new ByteArrayInputStream(data);
        var gZipInput = new GZIPInputStream(inputBytes);
        var outputBytes = new ByteArrayOutputStream();
        gZipInput.transferTo(outputBytes);

        return outputBytes.toByteArray();
    }
}
