package dev.aj.applicationevents.ticker;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.aj.applicationevents.order.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {

    private final TicketRepository ticketRepository;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public void createTicket(OrderService.OrderCompletedEvent event) {
        Ticket ticket = Ticket.builder()
                .subject("Failed order number " + event.order().getId())
                .body(String.format("Order failed => %s", objectMapper.writeValueAsString(event.order())))
                .build();

        ticketRepository.save(ticket);
    }
}
