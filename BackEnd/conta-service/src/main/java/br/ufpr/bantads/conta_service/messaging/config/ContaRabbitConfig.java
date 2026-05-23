package br.ufpr.bantads.conta_service.messaging.config;

import br.ufpr.bantads.conta_service.messaging.RabbitMQConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ContaRabbitConfig {

    public static final String FILA_MS = "fila-ms-conta";

    public static final String FILA_SAGA = "fila-saga";

    @Bean
    public Queue filaMs() {
        return new Queue(FILA_MS);
    }

    @Bean
    public Queue filaSaga() {
        return new Queue(FILA_SAGA);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter) {

        RabbitTemplate template =
                new RabbitTemplate(connectionFactory);

        template.setMessageConverter(jsonMessageConverter);

        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter) {

        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);

        factory.setMessageConverter(jsonMessageConverter);

        return factory;
    }

    @Bean
    public DirectExchange bantadsExchange() {
        return new DirectExchange(RabbitMQConstants.EXCHANGE);
    }

    @Bean
    public Queue contaCriarQueue() {
        return QueueBuilder
                .durable(RabbitMQConstants.CONTA_CRIAR_QUEUE)
                .build();
    }

    @Bean
    public Queue contaSyncQueue() {
        return QueueBuilder
                .durable(RabbitMQConstants.CONTA_SYNC_QUEUE)
                .build();
    }

    @Bean
    public Queue movimentacaoSyncQueue() {
        return QueueBuilder
                .durable(RabbitMQConstants.MOVIMENTACAO_SYNC_QUEUE)
                .build();
    }

    @Bean
    public Binding contaCriarBinding(
            Queue contaCriarQueue,
            DirectExchange bantadsExchange) {

        return BindingBuilder
                .bind(contaCriarQueue)
                .to(bantadsExchange)
                .with(RabbitMQConstants.CONTA_CRIAR_ROUTING_KEY);
    }

    @Bean
    public Binding contaSyncBinding(
            Queue contaSyncQueue,
            DirectExchange bantadsExchange) {

        return BindingBuilder
                .bind(contaSyncQueue)
                .to(bantadsExchange)
                .with(RabbitMQConstants.CONTA_SYNC_ROUTING_KEY);
    }

    @Bean
    public Binding movimentacaoSyncBinding(
            Queue movimentacaoSyncQueue,
            DirectExchange bantadsExchange) {

        return BindingBuilder
                .bind(movimentacaoSyncQueue)
                .to(bantadsExchange)
                .with(RabbitMQConstants.MOVIMENTACAO_SYNC_ROUTING_KEY);
    }
}