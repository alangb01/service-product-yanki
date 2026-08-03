package pe.nom.charlygastelo.app.yankiservice.support;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.MaybeEmitter;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleEmitter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class RequestResponseRegistry<T> {

    private final Map<String, MaybeEmitter<T>> pending = new ConcurrentHashMap<>();


    public Maybe<T> register(String correlationId) {
        return Maybe.<T>create(emitter -> {

                    MaybeEmitter<T> existing = pending.putIfAbsent(correlationId, emitter);

                    if (existing != null) {
                        emitter.onError(new IllegalStateException(
                                "CorrelationId already registered: " + correlationId
                        ));
                        return;
                    }

                    // Limpieza si el cliente cancela
                    emitter.setCancellable(() -> {
                        pending.remove(correlationId);
                        log.warn("[REGISTRY] Cancelled correlationId={}", correlationId);
                    });

                })
                // Timeout real
                .timeout(2, java.util.concurrent.TimeUnit.SECONDS)
                // Limpieza en timeout o error
                .doOnError(error -> {
                    pending.remove(correlationId);
                    log.error("[REGISTRY] Timeout or error for correlationId={}", correlationId);
                });
    }


    public void complete(String correlationId, T response) {
        MaybeEmitter<T> emitter = pending.remove(correlationId);

        if (emitter == null) {
            log.warn("[REGISTRY] No emitter found for correlationId={}", correlationId);
            return;
        }

        if (!emitter.isDisposed()) {
            emitter.onSuccess(response);
        }
    }

    public void fail(String correlationId, Throwable error) {
        MaybeEmitter<T> emitter = pending.remove(correlationId);

        if (emitter == null) {
            log.warn("[REGISTRY] No emitter found for correlationId={}", correlationId);
            return;
        }

        if (!emitter.isDisposed()) {
            emitter.onError(error);
        }
    }
}
