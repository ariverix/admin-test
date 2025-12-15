package edu.rutmiit.demo.demorest.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.UUID;

//@Component
@Order(1) // Устанавливаем высокий приоритет, чтобы фильтр сработал одним из первых
// Что значит 1 - чем меньше число, тем выше приоритет, фильтр сработает раньше других фильтров,
// которые могут быть определены в приложении как в конвеере
public class LoggingAndTracingFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(LoggingAndTracingFilter.class);
    private static final String CORRELATION_ID_HEADER = "X-Request-ID";
    private static final String CORRELATION_ID_MDC_KEY = "correlationId";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // Управление Correlation ID - извлекаем из заголовка или генерируем новый
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        if (!StringUtils.hasText(correlationId)) {
            correlationId = UUID.randomUUID().toString();
        }

        // Помещаем ID в MDC для обогащения всех логов
        MDC.put(CORRELATION_ID_MDC_KEY, correlationId);

        // Добавляем ID в заголовок ответа
        response.setHeader(CORRELATION_ID_HEADER, correlationId);

        long startTime = System.currentTimeMillis();

        try {
            // Логируем входящий запрос
            log.info("Request started: {} {}", request.getMethod(), request.getRequestURI());

            // Передаем управление дальше по цепочке фильтров и контроллеру
            filterChain.doFilter(request, response);

        } finally {
            long duration = System.currentTimeMillis() - startTime;
            // Логируем завершение запроса
            log.info("Request finished: {} {} with status {} in {}ms",
                    request.getMethod(), request.getRequestURI(), response.getStatus(), duration);

            // Критически важно! Очищаем MDC, чтобы избежать утечки контекста
            MDC.remove(CORRELATION_ID_MDC_KEY);
        }
    }
}
