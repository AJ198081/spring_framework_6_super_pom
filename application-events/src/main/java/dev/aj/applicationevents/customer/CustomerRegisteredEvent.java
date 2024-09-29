package dev.aj.applicationevents.customer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class CustomerRegisteredEvent {

    private final Customer customer;

}
