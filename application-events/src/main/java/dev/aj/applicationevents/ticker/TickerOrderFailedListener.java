package dev.aj.applicationevents.ticker;

import dev.aj.applicationevents.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class TickerOrderFailedListener {

    private final TicketService ticketService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void onOrderFailed(OrderService.OrderCompletedEvent event) {
        ticketService.createTicket(event);
    }
}
