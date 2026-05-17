package br.ufpr.bantads.saga_service.messaging.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SagaRabbitConfig {

    public static final String FILA_SAGA = "fila-saga";

    public static final String FILA_MS_GERENTE = "fila-ms-gerente";

    public static final String FILA_MS_CONTA = "fila-ms-conta";

    public static final String FILA_MS_CLIENTE = "fila-ms-cliente";

    @Bean
    public Queue filaSaga() {
        return new Queue(FILA_SAGA);
    }

    @Bean
    public Queue filaMsGerente() {
        return new Queue(FILA_MS_GERENTE);
    }

    @Bean
    public Queue filaMsConta() {
        return new Queue(FILA_MS_CONTA);
    }

    @Bean
    public Queue filaMsCliente() {
        return new Queue(FILA_MS_CLIENTE);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {

        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory, MessageConverter messageConverter) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);

        return factory;
    }
}
