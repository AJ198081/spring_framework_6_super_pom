package dev.aj.applicationevents.email;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EmailService {
    private final EmailRepository repository;

    public Email sendEmail(Email email) {
        return repository.save(email);
    }
}
