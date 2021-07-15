package ca.bc.gov.ag.efax.mail.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import ca.bc.gov.ag.efax.BaseTestSuite;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.property.complex.ItemId;
import microsoft.exchange.webservices.data.search.FindItemsResults;

public class EmailServiceTest extends BaseTestSuite {

    @Autowired
    private EmailService emailService;

    private FindItemsResults<Item> items;

    @BeforeEach
    @Override
    protected void beforeEach() throws Exception {
        super.beforeEach();
        
        EmailMessage email1 = new EmailMessage(exchangeService);
        EmailMessage email2 = new EmailMessage(exchangeService);

        // creates a UUID in the emailMessage
        exchangeService.createItem(email1, null, null, null);
        exchangeService.createItem(email2, null, null, null);

        // Stub out Exchange Server to return 2 emails when findItems is called.
        items = new FindItemsResults<Item>();
        List<EmailMessage> emails = new ArrayList<EmailMessage>();
        emails.add(email1);
        emails.add(email2);
        ReflectionTestUtils.setField(items, "items", emails);
        when(exchangeService.findItems(eq(WellKnownFolderName.Inbox), anyString(), any())).thenReturn(items);
        
        // Stub out Exchange Server to delete an email when deleteItem is called.
        doAnswer(invocation -> {
            ItemId itemId = invocation.getArgument(0);
            for (Iterator<EmailMessage> iter = emails.iterator(); iter.hasNext();) {
                EmailMessage emailMessage = iter.next();
                if (emailMessage.getId().equals(itemId)) {
                    iter.remove();
                    break;
                }                
            }
            return null;
        }).when(exchangeService).deleteItem(any(), any(), any(), any());
    }

    @Test
    void testGetInboxEmails() throws Exception {
        // Assert the emailService returns these items.
        List<EmailMessage> inboxEmails = emailService.getInboxEmails();
        assertEquals(2, inboxEmails.size());
        assertEquals(items.getItems().get(0).getId().getUniqueId(), inboxEmails.get(0).getId().getUniqueId());
        assertEquals(items.getItems().get(1).getId().getUniqueId(), inboxEmails.get(1).getId().getUniqueId());
    }

    @Test
    void testDeleteEmail() throws Exception {
        List<EmailMessage> inboxEmails = emailService.getInboxEmails();
        assertEquals(2, inboxEmails.size());
        EmailMessage email1 = inboxEmails.get(0);
        EmailMessage email2 = inboxEmails.get(1);
        
        // Delete the first email
        emailService.deleteEmail(email1);

        // Assert the deletion occurred
        inboxEmails = emailService.getInboxEmails();
        assertEquals(1, inboxEmails.size());
        assertEquals(email2.getId().getUniqueId(), inboxEmails.get(0).getId().getUniqueId());
    }

}
