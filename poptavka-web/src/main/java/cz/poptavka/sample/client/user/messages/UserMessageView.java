package cz.poptavka.sample.client.user.messages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import cz.poptavka.sample.shared.domain.FakeMessage;

public class UserMessageView extends Composite implements HasClickHandlers {

    private static final int DESCRIPTION_TD = 3;

    private static UserMessageViewUiBinder uiBinder = GWT
            .create(UserMessageViewUiBinder.class);

    interface UserMessageViewUiBinder extends
            UiBinder<Widget, UserMessageView> {
    }


    public interface MessageStyle extends CssResource {
        @ClassName("message")
        String message();

        @ClassName("message-first")
        String messageFirst();

        @ClassName("message-last")
        String messageLast();

        @ClassName("message-opened")
        String messageOpened();

        @ClassName("message-header")
        String messageHeader();

        @ClassName("message-body")
        String messageBody();

        @ClassName("action-button")
        String actionButton();

    }

    @UiField MessageStyle style;

    @UiField Element header;
    @UiField Element body;
    @UiField Element headerTable;
    @UiField Element messagePreview;

    @UiField Anchor replyButton;

    private boolean collapsed = false;


    /**
     * Constructs object of user message with custom parameter
     *
     * @param message message to fill the view
     * @param isFirst if true, message is set as first, if false message is set as last!
     */
    public UserMessageView(FakeMessage message, boolean isFirst) {
        this(message);
        if (isFirst) {
            header.getParentElement().addClassName(style.messageFirst());
        } else {
            header.getParentElement().addClassName(style.messageLast());
            // Get buttons list, and set Eventhandlers for them;
            // According to DOM specification and widget structure, it's the last child div of
            // parent div. It's NOT the last child, because last ist the textNode after that div.
            // That means childCount - 2
            Element actionButtonBar = (Element) body.getChild(body.getChildCount() - 2);
            actionButtonBar.getStyle().setDisplay(Display.BLOCK);
//            DOM.sinkEvents(elem, eventBits);
        }
    }

    public UserMessageView(FakeMessage message) {
        initWidget(uiBinder.createAndBindUi(this));
        // set headerTable 100% width
        Element messagePart =  (Element) headerTable.getChild(DESCRIPTION_TD);
        messagePart.setAttribute("width", "100%");
        // set header data
        NodeList<Element> tableColumns = headerTable.getElementsByTagName("td");
        // author
        tableColumns.getItem(0).setInnerText(message.getAuthor());
        GWT.log(tableColumns.getItem(2).getClassName());

        // message
        messagePreview.setInnerText(message.getMessageBody());

        // date
        tableColumns.getItem(3).setInnerText(message.getDate());

//        header.getElementsByTagName("div").getItem(0).setInnerText(message.getDate());
        // the first child is our content place
        body.getElementsByTagName("div").getItem(0).setInnerHTML(message.getMessageBody());


        // TODO
        replyButton.getElement().getStyle().setBackgroundImage("-moz-linear-gradient(center bottom,rgb(46,45,46) 4%,"
                + "rgb(122,118,122) 54%");
    }

    public void toggleMessageBody() {
        if (collapsed) {
            body.getStyle().setDisplay(Display.BLOCK);
        } else {
            body.getStyle().setDisplay(Display.NONE);
        }
        collapsed = !collapsed;
    }

    @Override
    protected void onLoad() {
        com.google.gwt.user.client.Element castedElement = castElement(header);
        DOM.sinkEvents(castedElement, Event.ONCLICK);
        DOM.setEventListener(castedElement, new MessageToggleHangler());
    }

    private class MessageToggleHangler implements EventListener {
        @Override
        public void onBrowserEvent(Event event) {
            event.preventDefault();
            event.stopPropagation();
            if (event.getTypeInt() == Event.ONCLICK) {
                toggleMessageBody();
            }
        }
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        DOM.setEventListener(castElement(header), null);
    }

    private com.google.gwt.user.client.Element castElement(Element elem) {
        return (com.google.gwt.user.client.Element) elem;
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addDomHandler(handler, ClickEvent.getType());
    }

    public Anchor getReplyButton() {
        return replyButton;
    }

}