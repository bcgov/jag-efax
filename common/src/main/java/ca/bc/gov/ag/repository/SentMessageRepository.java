package ca.bc.gov.ag.repository;

import org.springframework.data.repository.CrudRepository;

import ca.bc.gov.ag.model.SentMessage;

public interface SentMessageRepository extends CrudRepository<SentMessage, String>{


}
