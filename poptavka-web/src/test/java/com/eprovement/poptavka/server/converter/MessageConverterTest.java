/*
 * Copyright (C) 2011, eProvement s.r.o. All rights reserved.
 */
package com.eprovement.poptavka.server.converter;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import com.eprovement.poptavka.base.BasicIntegrationTest;
import com.eprovement.poptavka.domain.enums.MessageState;
import com.eprovement.poptavka.domain.message.Message;
import com.eprovement.poptavka.domain.message.UserMessage;
import com.eprovement.poptavka.domain.user.User;
import com.eprovement.poptavka.shared.domain.message.MessageDetail;
import com.eprovement.poptavka.shared.domain.type.MessageType;
import org.apache.commons.lang.Validate;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class MessageConverterTest extends BasicIntegrationTest {

    private static final String SENDER_EMAIL = "sender@test.com";
    /** Converters to be tested. **/
    @Autowired
    @Qualifier("userMessageConverter")
    Converter<UserMessage, MessageDetail> userMessageConverter;
    @Autowired
    @Qualifier("messageConverter")
    Converter<Message, MessageDetail> messageConverter;
    /** Constants. **/
    private static final Boolean READ = true;
    private static final Boolean STARRED = false;
    private static final Long USER_MESSAGE_ID = 11L;
    private static final Long MESSAGE_ID = 111L;
    private static final Long THREADROOT_ID = 112L;
    private static final Long PARENT_ID = 113L;
    private static final Long SENDER_ID = 114L;
    private static final String SUBJECT = "Test convertora.";
    private static final String BODY = "Test convertora Body.";
    private static final String DATE_FORMAT = "dd.MM.yyyy";
    private static final String DATE = "21.12.2012";
    private static final SimpleDateFormat SDF = new SimpleDateFormat(DATE_FORMAT);

    @Test
    public void testConvertToUserMessageTarget() throws Exception {

        Validate.notNull(userMessageConverter);
        final UserMessage userMessage = createUserMessage();
        assertNotNull(userMessage);

        final MessageDetail messageDetail = userMessageConverter.convertToTarget(userMessage);
        assertNotNull(messageDetail);

        //UserMessage
        assertThat(messageDetail.isRead(), is(READ));
        assertThat(messageDetail.isStarred(), is(STARRED));
        assertThat(messageDetail.getUserMessageId(), is(USER_MESSAGE_ID));
        //Message
        checkMessage(messageDetail);

        assertThat(SDF.format(messageDetail.getCreated().getTime()), is(DATE));
        assertThat(SDF.format(messageDetail.getSent()), is(DATE));
    }

    @Test
    public void testConvertToMessageTarget() throws Exception {
        Validate.notNull(messageConverter);
        final Message message = createMessage();
        assertNotNull(message);

        final MessageDetail messageDetail = messageConverter.convertToTarget(message);
        assertNotNull(messageDetail);

        //Message
        checkMessage(messageDetail);

        assertThat(SDF.format(messageDetail.getCreated()), is(DATE));
        assertThat(SDF.format(messageDetail.getSent()), is(DATE));
    }

    //--------------------------------------------------- HELPER METHODS -----------------------------------------------

    private UserMessage createUserMessage() {
        final UserMessage userMessage = new UserMessage();
        //UserMessage
        userMessage.setRead(READ);
        userMessage.setStarred(STARRED);
        userMessage.setId(USER_MESSAGE_ID);

        //set message and return
        userMessage.setMessage(createMessage());
        return userMessage;
    }

    private Message createMessage() {
        try {
            Message message = new Message();
            message.setMessageState(MessageState.COMPOSED);
            message.setId(MESSAGE_ID);

            Message threadRoot = new Message();
            threadRoot.setId(THREADROOT_ID);
            message.setThreadRoot(threadRoot);

            Message parrent = new Message();
            parrent.setId(PARENT_ID);
            message.setParent(parrent);

            User sender = new User();
            sender.setId(SENDER_ID);
            sender.setEmail(SENDER_EMAIL);
            message.setSender(sender);

            message.setSubject(SUBJECT);
            message.setBody(BODY);
            message.setCreated(SDF.parse(DATE));
            message.setSent(SDF.parse(DATE));

            return message;
        } catch (ParseException ex) {
            throw new RuntimeException("Cannot create message", ex);
        }
    }


    private void checkMessage(MessageDetail messageDetail) {
        assertThat(messageDetail.getMessageId(), is(MESSAGE_ID));
        assertThat(messageDetail.getThreadRootId(), is(THREADROOT_ID));
        assertThat(messageDetail.getParentId(), is(PARENT_ID));
        assertThat(messageDetail.getSenderId(), is(SENDER_ID));

        assertThat(messageDetail.getSubject(), is(SUBJECT));
        assertThat(messageDetail.getBody(), is(BODY));
        assertThat(messageDetail.getMessageState(), is(MessageState.COMPOSED.name()));
        assertThat(messageDetail.getMessageType(), is(MessageType.CONVERSATION.name()));
        assertThat(messageDetail.getSender(), is(SENDER_EMAIL));
    }
}
