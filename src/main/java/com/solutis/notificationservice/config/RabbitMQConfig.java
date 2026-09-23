package com.solutis.notificationservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String TICKET_EXCHANGE = "ticket.exchange";

    public static final String TICKET_CREATED_QUEUE =
            "ticket.created.queue";

    public static final String TICKET_ASSIGNED_QUEUE =
            "ticket.assigned.queue";

    public static final String TICKET_STATUS_CHANGED_QUEUE =
            "ticket.status-changed.queue";

    public static final String TICKET_CREATED_ROUTING_KEY =
            "ticket.created";

    public static final String TICKET_ASSIGNED_ROUTING_KEY =
            "ticket.assigned";

    public static final String TICKET_STATUS_CHANGED_ROUTING_KEY =
            "ticket.status-changed";

    @Bean
    public TopicExchange ticketExchange() {
        return new TopicExchange(TICKET_EXCHANGE);
    }

    @Bean
    public Queue ticketCreatedQueue() {
        return new Queue(TICKET_CREATED_QUEUE, true);
    }

    @Bean
    public Queue ticketAssignedQueue() {
        return new Queue(TICKET_ASSIGNED_QUEUE, true);
    }

    @Bean
    public Queue ticketStatusChangedQueue() {
        return new Queue(TICKET_STATUS_CHANGED_QUEUE, true);
    }

    @Bean
    public Binding ticketCreatedBinding(
            Queue ticketCreatedQueue,
            TopicExchange ticketExchange
    ) {
        return BindingBuilder
                .bind(ticketCreatedQueue)
                .to(ticketExchange)
                .with(TICKET_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding ticketAssignedBinding(
            Queue ticketAssignedQueue,
            TopicExchange ticketExchange
    ) {
        return BindingBuilder
                .bind(ticketAssignedQueue)
                .to(ticketExchange)
                .with(TICKET_ASSIGNED_ROUTING_KEY);
    }

    @Bean
    public Binding ticketStatusChangedBinding(
            Queue ticketStatusChangedQueue,
            TopicExchange ticketExchange
    ) {
        return BindingBuilder
                .bind(ticketStatusChangedQueue)
                .to(ticketExchange)
                .with(TICKET_STATUS_CHANGED_ROUTING_KEY);
    }

    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}