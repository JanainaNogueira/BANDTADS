package br.ufpr.bantads.gerente_service.service;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SagaSyncService {

    private final ConcurrentHashMap<String, CompletableFuture<Void>> sagas =
            new ConcurrentHashMap<>();

    public CompletableFuture<Void> criarSaga(String idSaga) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        sagas.put(idSaga, future);
        return future;
    }

    public void concluirSaga(String idSaga) {
        CompletableFuture<Void> future = sagas.remove(idSaga);

        if (future != null) {
            future.complete(null);
        }
    }

    public void erroSaga(String idSaga, String erro) {
        CompletableFuture<Void> future = sagas.remove(idSaga);

        if (future != null) {
            future.completeExceptionally(
                    new RuntimeException(erro)
            );
        }
    }
}
