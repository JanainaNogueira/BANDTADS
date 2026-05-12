package br.ufpr.bantads.conta_service.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
// import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ContaRabbitConfig {

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
            // SimpleRabbitListenerContainerFactoryConfigurer configurer,
            ConnectionFactory connectionFactory,
            MessageConverter rabbitMessageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        // configurer.configure(factory, connectionFactory);
        factory.setMessageConverter(rabbitMessageConverter);
        return factory;
    }

    @Bean
    public DirectExchange bantadsExchange() {
        return new DirectExchange(RabbitMQConstants.EXCHANGE);
    }

    @Bean
    public Queue contaCriarQueue() {
        return QueueBuilder.durable(RabbitMQConstants.CONTA_CRIAR_QUEUE).build();
    }

    @Bean
    public Queue contaSyncQueue() {
        return QueueBuilder.durable(RabbitMQConstants.CONTA_SYNC_QUEUE).build();
    }

    @Bean
    public Queue movimentacaoSyncQueue() {
        return QueueBuilder.durable(RabbitMQConstants.MOVIMENTACAO_SYNC_QUEUE).build();
    }

    @Bean
    public Binding contaCriarBinding(Queue contaCriarQueue, DirectExchange bantadsExchange) {
        return BindingBuilder.bind(contaCriarQueue)
                .to(bantadsExchange)
                .with(RabbitMQConstants.CONTA_CRIAR_ROUTING_KEY);
    }

    @Bean
    public Binding contaSyncBinding(Queue contaSyncQueue, DirectExchange bantadsExchange) {
        return BindingBuilder.bind(contaSyncQueue)
                .to(bantadsExchange)
                .with(RabbitMQConstants.CONTA_SYNC_ROUTING_KEY);
    }

    @Bean
    public Binding movimentacaoSyncBinding(Queue movimentacaoSyncQueue, DirectExchange bantadsExchange) {
        return BindingBuilder.bind(movimentacaoSyncQueue)
                .to(bantadsExchange)
                .with(RabbitMQConstants.MOVIMENTACAO_SYNC_ROUTING_KEY);
    }
}
