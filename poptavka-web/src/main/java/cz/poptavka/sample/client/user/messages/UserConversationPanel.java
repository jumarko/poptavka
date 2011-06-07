package cz.poptavka.sample.client.user.messages;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

import cz.poptavka.sample.client.user.messages.SimpleMessageWindow.MessageDisplayType;
import cz.poptavka.sample.shared.domain.MessageDetail;

/**
 * SimpleMessageWindow holder, enabling features:
 * <ul>
 * TODO
 * <li>collapsing</li>
 * TODO
 * <li>expanding</li>
 * TODO
 * <li>hiding</li>
 * TODO
 * <li>displaying tree of messages</li>
 * </ul>
 * Will need  presenter later, when handling this events.
 * (maybe not, if actions will be bind in wrapper presenter)
 *
 * @author Beho
 *
 */
public class UserConversationPanel extends Composite {

    private static UserConversationPanelUiBinder uiBinder = GWT.create(UserConversationPanelUiBinder.class);
    interface UserConversationPanelUiBinder extends UiBinder<Widget, UserConversationPanel> {
    }

    private ArrayList<OfferWindowPresenter> offerPresenters = new ArrayList<OfferWindowPresenter>();

    @UiField FlowPanel messagePanel;

    ClickHandler acceptHandler = null;
    ClickHandler replyHandler = null;
    ClickHandler deleteHandler = null;

    private int messageCount = 0;
    private MessageDetail replyToMessage;

    public UserConversationPanel() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    /**
     * Display list of messages. When messages are set, control panell should be displayed as well.
     * Message List size is at least always 1
     *
     * @param messages list of messages to be displayed
     */
    public void setMessageList(ArrayList<MessageDetail> messages, boolean collapsed) {
        messagePanel.clear();

        // Last message is visible, when there are more messages
        // last message is always stored for reply
        replyToMessage = messages.get(messages.size() - 1);

        //local
        boolean moreThanOneVisibleMessage = false;

        if (messages.size() > 1) {
            messagePanel.add(new SimpleMessageWindow(messages.get(1), collapsed, MessageDisplayType.FIRST));
            for (int i = 2; i < (messages.size() - 1); i++) {
                messagePanel.add(new SimpleMessageWindow(messages.get(i), collapsed));
                moreThanOneVisibleMessage = true;
            }
            if (moreThanOneVisibleMessage) {
                messagePanel.add(new SimpleMessageWindow(replyToMessage, false, MessageDisplayType.LAST));
            }
        }

        messageCount = messagePanel.getWidgetCount();

        if (messageCount == 1) {
            ((SimpleMessageWindow) messagePanel.getWidget(0)).setMessageStyle(MessageDisplayType.BOTH);
        }
    }

    public void addMessage(MessageDetail lastMessage) {
        if (messageCount > 0) {
            SimpleMessageWindow last = (SimpleMessageWindow) messagePanel.getWidget(messageCount - 1);
            last.setMessageStyle(MessageDisplayType.NORMAL);
        }
        messagePanel.add(new SimpleMessageWindow(lastMessage, false, MessageDisplayType.LAST));
        replyToMessage = lastMessage;


        messageCount = messagePanel.getWidgetCount();
    }

    public void addOfferMessagePresenter(OfferWindowPresenter presenter) {
        offerPresenters.add(presenter);
        messagePanel.add(presenter.getWidgetView());
    }

    public ArrayList<OfferWindowPresenter> clearContent() {
        return offerPresenters;
    }

    /**
     * Received the message being sent and fills it with necessary attributes, from stored message.
     *
     * @param messageDetail message being sent
     * @return updated message
     */
    public MessageDetail updateSendingMessage(MessageDetail messageDetail) {
        messageDetail.setThreadRootId(replyToMessage.getThreadRootId());
        messageDetail.setReceiverId(replyToMessage.getSenderId());
        messageDetail.setParentId(replyToMessage.getMessageId());
        return messageDetail;
    }

}
