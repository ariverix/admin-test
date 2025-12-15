package ws.demo.demo.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(NotificationWebSocketHandler.class);

    // ConcurrentHashMap.newKeySet() — потокобезопасный Set,
    // эффективнее CopyOnWriteArrayList при частых изменениях
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        log.info("Новое подключение: id={}, всего активных: {}",
                session.getId(), sessions.size());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        log.debug("Сообщение от {}: {}", session.getId(), payload);

        // Пример: echo-ответ или обработка команд
        // if ("PING".equals(payload)) { sendMessage(session, "PONG"); }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        log.info("Отключение: id={}, причина={}, осталось: {}",
                session.getId(), status.getReason(), sessions.size());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("Ошибка транспорта для сессии {}: {}",
                session.getId(), exception.getMessage());
        sessions.remove(session);
    }

    /**
     * Рассылка сообщения всем подключенным клиентам.
     * Возвращает количество успешно отправленных сообщений.
     */
    public int broadcast(String message) {
        TextMessage textMessage = new TextMessage(message);
        int sent = 0;

        for (WebSocketSession session : sessions) {
            if (sendMessage(session, textMessage)) {
                sent++;
            }
        }

        log.info("Broadcast: отправлено {}/{} клиентам", sent, sessions.size());
        return sent;
    }

    private boolean sendMessage(WebSocketSession session, TextMessage message) {
        if (!session.isOpen()) {
            sessions.remove(session);
            return false;
        }
        try {
            // ConcurrentWebSocketSessionDecorator — альтернатива для высоких нагрузок
            session.sendMessage(message);
            return true;
        } catch (IOException e) {
            log.warn("Ошибка отправки в сессию {}: {}", session.getId(), e.getMessage());
            sessions.remove(session);
            return false;
        }
    }

    /** Количество активных подключений (для мониторинга) */
    public int getActiveConnections() {
        return sessions.size();
    }
}
