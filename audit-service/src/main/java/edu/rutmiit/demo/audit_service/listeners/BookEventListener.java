package edu.rutmiit.demo.audit_service.listeners;

import edu.rutmiit.demo.events.BookCreatedEvent;
import edu.rutmiit.demo.events.UserRatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class BookEventListener {

    private static final Logger log = LoggerFactory.getLogger(BookEventListener.class);

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "notification-queue", durable = "true"),
            exchange = @Exchange(name = "books-exchange", type = "topic"),
            key = "book.created"
    ))
    public void handleBookCreatedEvent(BookCreatedEvent event) {
        log.info("Received new book event: {}.", event);
        // Здесь могла бы быть логика аудита или уведомлений
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    // Уникальное имя очереди для уведомлений!
                    value = @Queue(name = "q.audit.analytics", durable = "true"),
                    exchange = @Exchange(name = "analytics-fanout", type = "fanout")
            )
    )
    public void handleRating(UserRatedEvent event) {
        log.info("NOTIFY: Sending email. User {} has new rating: {}", event.userId(), event.score());
    }

}
