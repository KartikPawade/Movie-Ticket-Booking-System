package com.movienow.org.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    @Value("${rabbitmq.email.queue}")
    private String emailQueueName;
    @Value("${rabbitmq.email.exchange.name}")
    private String emailExchangeName;
    @Value("${rabbitmq.email.binding.key}")
    private String emailBindingKey;

    @Value("${rabbitmq.payment.details.unsaved.queue}")
    private String unsavedPaymentDetailsQueue;
    @Value("${rabbitmq.payment.details.unsaved.exchange.name}")
    private String unsavedPaymentDetailsExchange;
    @Value("${rabbitmq.payment.details.unsaved.binding.key}")
    private String unsavedPaymentDetailsBindingKey;


    @Bean
    public Queue emailQueue() {
        return new Queue(emailQueueName);
    }

    @Bean
    public TopicExchange emailTopicExchange() {
        return new TopicExchange(emailExchangeName);
    }

    @Bean
    public Binding emailBinding() {
        return BindingBuilder.bind(emailQueue()).to(emailTopicExchange()).with(emailBindingKey);
    }

    @Bean
    public Queue unsavedPaymentDetailsQueue() {
        return new Queue(unsavedPaymentDetailsQueue);
    }
    @Bean
    public TopicExchange unsavedPaymentDetailsExchange() {
        return new TopicExchange(unsavedPaymentDetailsExchange);
    }
    @Bean
    public Binding unsavedPaymentDetailsBinding() {
        return BindingBuilder.bind(unsavedPaymentDetailsQueue()).to(unsavedPaymentDetailsExchange()).with(unsavedPaymentDetailsBindingKey);
    }

    @Bean
    public RabbitTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setMessageConverter(messageConverter());
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
