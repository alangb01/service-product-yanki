package pe.nom.charlygastelo.app.yankiservice.support;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleEmitter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class RequestResponseRegistry<T> {

    private final Map<String, SingleEmitter<T>> pending = new ConcurrentHashMap<>();

    public Single<T> register(String correlationId) {
        return Single.create(emitter -> {
            SingleEmitter<T> existing = pending.putIfAbsent(correlationId, emitter);

            if (existing != null) {
                emitter.onError(new IllegalStateException(
                        "CorrelationId already registered: " + correlationId
                ));
                return;
            }

            emitter.setCancellable(() -> pending.remove(correlationId));
        });
    }

    public void complete(String correlationId, T response) {
        SingleEmitter<T> emitter = pending.remove(correlationId);

        if (emitter == null) {
            log.warn("[REGISTRY] No emitter found for correlationId={}", correlationId);
            return;
        }

        if (!emitter.isDisposed()) {
            emitter.onSuccess(response);
        }
    }

    public void fail(String correlationId, Throwable error) {
        SingleEmitter<T> emitter = pending.remove(correlationId);

        if (emitter == null) {
            log.warn("[REGISTRY] No emitter found for correlationId={}", correlationId);
            return;
        }

        if (!emitter.isDisposed()) {
            emitter.onError(error);
        }
    }
}
