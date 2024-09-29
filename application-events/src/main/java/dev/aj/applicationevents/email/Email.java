package dev.aj.applicationevents.email;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Email {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String toEmail;
    private String fromEmail;
    private String subject;
    private String body;

}
