package pe.nom.charlygastelo.app.yankiservice.infrastructure.events;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.apache.avro.Schema;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;

@Component
public class AvroJsonDeserializer {

    public <T extends SpecificRecordBase> T deserialize(
            String payload,
            Class<T> clazz,
            Schema schema) throws Exception {

        ByteArrayInputStream inputStream =
                new ByteArrayInputStream(payload.getBytes(StandardCharsets.UTF_8));

        Decoder decoder = DecoderFactory
                .get()
                .jsonDecoder(schema, inputStream);

        SpecificDatumReader<T> reader =
                new SpecificDatumReader<>(schema);

        return reader.read(null, decoder);
    }
}