package ca.bc.gov.ag.efax.mail.repository;

import org.springframework.data.repository.CrudRepository;

import ca.bc.gov.ag.efax.mail.model.SentMessage;

public interface SentMessageRepository extends CrudRepository<SentMessage, String>{


}
