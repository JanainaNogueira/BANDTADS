package br.ufpr.bantads.gerente_service.read.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.ufpr.bantads.gerente_service.model.Gerente;
import br.ufpr.bantads.gerente_service.read.model.GerenteClientesView;
import br.ufpr.bantads.gerente_service.read.repository.GerenteClientesRepository;

@Component
public class GerenteReadConsumer {

    @Autowired
    private GerenteClientesRepository repository;

    //  quando gerente é criado
    @RabbitListener(queues = "gerente-criado-queue")
    public void gerenteCriado(Gerente gerente) {

        GerenteClientesView view = new GerenteClientesView();
        view.setGerenteId(gerente.getId());
        view.setNome(gerente.getNome());
        view.setEmail(gerente.getEmail());

        repository.save(view);
    }

    //  quando cliente é criado/vinculado
    // @RabbitListener(queues = "cliente-criado-queue")
    // public void clienteCriado(ClienteDTO cliente) {

    //     GerenteClientesView view =
    //             repository.findById(cliente.getGerenteId())
    //             .orElse(null);

    //     if (view != null) {
    //         view.getClientes().add(cliente.getNome());
    //         repository.save(view);
    //     }
    // }
}