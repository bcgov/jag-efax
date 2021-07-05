package ca.bc.gov.ag.efax.mail.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.enumeration.search.SortDirection;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.core.service.schema.ItemSchema;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import microsoft.exchange.webservices.data.search.ItemView;

@Service
public class EmailServiceImpl implements EmailService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ExchangeServiceFactory exchangeServiceFactory;
    
    @Override
    public List<EmailMessage> getEfaxInboxEmails() throws Exception {    
        logger.trace("Retrieving inbox emails");
        ItemView view = new ItemView(Integer.MAX_VALUE);
        view.getOrderBy().add(ItemSchema.DateTimeReceived, SortDirection.Ascending);
        
        ExchangeService exchangeService = exchangeServiceFactory.createService();
        FindItemsResults<Item> emails = exchangeService.findItems(WellKnownFolderName.Inbox, view);

        List<EmailMessage> emailMessages = emails.getItems().stream().map(item -> (EmailMessage) item).collect(Collectors.toList());
        logger.trace("Retrieved {} emails", emailMessages.size());
        return emailMessages;
    }

}
