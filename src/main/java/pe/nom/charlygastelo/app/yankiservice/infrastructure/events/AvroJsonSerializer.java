package pe.nom.charlygastelo.app.yankiservice.infrastructure.events;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;

@Component
public class AvroJsonSerializer {

    public String serialize(SpecificRecordBase event) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Encoder encoder = EncoderFactory
                .get()
                .jsonEncoder(event.getSchema(), outputStream);

        SpecificDatumWriter<SpecificRecordBase> writer =
                new SpecificDatumWriter<>(event.getSchema());

        writer.write(event, encoder);
        encoder.flush();

        return outputStream.toString(StandardCharsets.UTF_8);
    }
}