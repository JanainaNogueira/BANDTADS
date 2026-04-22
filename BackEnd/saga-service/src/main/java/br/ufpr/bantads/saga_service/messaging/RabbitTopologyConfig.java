package br.ufpr.bantads.saga_service.messaging;

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
public class RabbitTopologyConfig {

    @Bean
    public MessageConverter rabbitMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter rabbitMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(rabbitMessageConverter);
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter rabbitMessageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(rabbitMessageConverter);
        return factory;
    }

    @Bean
    public DirectExchange bantadsExchange() {
        return new DirectExchange(RabbitMQConstants.EXCHANGE);
    }

    @Bean
    public Queue sagaClienteAprovadoQueue() {
        return QueueBuilder.durable(RabbitMQConstants.SAGA_CLIENTE_APROVADO_QUEUE).build();
    }

    @Bean
    public Queue sagaContaCriadaQueue() {
        return QueueBuilder.durable(RabbitMQConstants.SAGA_CONTA_CRIADA_QUEUE).build();
    }

    @Bean
    public Queue sagaContaFalhaQueue() {
        return QueueBuilder.durable(RabbitMQConstants.SAGA_CONTA_FALHA_QUEUE).build();
    }

    @Bean
    public Queue contaCriarQueue() {
        return QueueBuilder.durable(RabbitMQConstants.CONTA_CRIAR_QUEUE).build();
    }

    @Bean
    public Queue clienteReprovarQueue() {
        return QueueBuilder.durable(RabbitMQConstants.CLIENTE_REPROVAR_QUEUE).build();
    }

    @Bean
    public Binding sagaClienteAprovadoBinding(Queue sagaClienteAprovadoQueue, DirectExchange bantadsExchange) {
        return BindingBuilder.bind(sagaClienteAprovadoQueue)
                .to(bantadsExchange)
                .with(RabbitMQConstants.CLIENTE_APROVADO_ROUTING_KEY);
    }

    @Bean
    public Binding sagaContaCriadaBinding(Queue sagaContaCriadaQueue, DirectExchange bantadsExchange) {
        return BindingBuilder.bind(sagaContaCriadaQueue)
                .to(bantadsExchange)
                .with(RabbitMQConstants.CONTA_CRIADA_ROUTING_KEY);
    }

    @Bean
    public Binding sagaContaFalhaBinding(Queue sagaContaFalhaQueue, DirectExchange bantadsExchange) {
        return BindingBuilder.bind(sagaContaFalhaQueue)
                .to(bantadsExchange)
                .with(RabbitMQConstants.CONTA_FALHA_ROUTING_KEY);
    }

    @Bean
    public Binding contaCriarBinding(Queue contaCriarQueue, DirectExchange bantadsExchange) {
        return BindingBuilder.bind(contaCriarQueue)
                .to(bantadsExchange)
                .with(RabbitMQConstants.CONTA_CRIAR_ROUTING_KEY);
    }

    @Bean
    public Binding clienteReprovarBinding(Queue clienteReprovarQueue, DirectExchange bantadsExchange) {
        return BindingBuilder.bind(clienteReprovarQueue)
                .to(bantadsExchange)
                .with(RabbitMQConstants.CLIENTE_REPROVAR_ROUTING_KEY);
    }
}
