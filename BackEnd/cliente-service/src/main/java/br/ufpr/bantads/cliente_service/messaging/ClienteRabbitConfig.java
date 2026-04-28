package br.ufpr.bantads.cliente_service.messaging;

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
public class ClienteRabbitConfig {

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
    public Queue clienteReprovarQueue() {
        return QueueBuilder.durable(RabbitMQConstants.CLIENTE_REPROVAR_QUEUE).build();
    }

    @Bean
    public Binding clienteReprovarBinding(Queue clienteReprovarQueue, DirectExchange bantadsExchange) {
        return BindingBuilder
                .bind(clienteReprovarQueue)
                .to(bantadsExchange)
                .with(RabbitMQConstants.CLIENTE_REPROVAR_ROUTING_KEY);
    }
}
