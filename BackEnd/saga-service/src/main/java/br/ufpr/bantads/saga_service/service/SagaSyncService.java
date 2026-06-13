package br.ufpr.bantads.saga_service.service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;


@Service
public class SagaSyncService {

    private final Map<String, CompletableFuture<Object>> futuros = new ConcurrentHashMap<>();

    public CompletableFuture<Object> criarSaga(String idSaga) {
        CompletableFuture<Object> future = new CompletableFuture<>();
        futuros.put(idSaga, future);
        return future;
    }

    public void concluirSaga(String idSaga, Object resultado) {
        CompletableFuture<Object> future = futuros.remove(idSaga);
        if (future != null) {
            future.complete(resultado);
        }
    }

    public void falharSaga(String idSaga, String motivo) {
        CompletableFuture<Object> future = futuros.remove(idSaga);
        if (future != null) {
            future.completeExceptionally(new RuntimeException(motivo));
        }
    }
}
