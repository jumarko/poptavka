package cz.poptavka.sample.client.user.messages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class ReplyWindow extends Composite implements ReplyWindowPresenter.ReplyInterface {

    private static ReplyWindowUiBinder uiBinder = GWT.create(ReplyWindowUiBinder.class);
    interface ReplyWindowUiBinder extends UiBinder<Widget, ReplyWindow> {   }

    public interface ReplyStyle extends CssResource {
        @ClassName("reply-message")
        String replyMessage();

//        @ClassName("reply-message-arrow-border")
//        String replyMessageArrowBorder();
//
//        @ClassName("reply-message-arrow")
//        String replyMessageArrow();

        @ClassName("text-area")
        String textArea();
    }

    @UiField Element header;
    @UiField TextArea replyTextArea;
    @UiField Anchor submitBtn;
    @UiField Anchor cancelBtn;
    //main widget part is hidden
    private boolean hiddenReplyBody = true;

    //replying message related stuff
    private Long messageToReplyId;

    @Override
    public void createView() {
        initWidget(uiBinder.createAndBindUi(this));
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
                toggleReplyBody();
            }
        }
    }

    private void toggleReplyBody() {
        if (hiddenReplyBody) {
            header.getStyle().setDisplay(Display.NONE);
            header.getNextSiblingElement().getStyle().setDisplay(Display.BLOCK);
        } else {
            header.getStyle().setDisplay(Display.BLOCK);
            header.getNextSiblingElement().getStyle().setDisplay(Display.NONE);
            replyTextArea.setValue("");
        }
        hiddenReplyBody = !hiddenReplyBody;
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        DOM.setEventListener(castElement(header), null);
    }

    private com.google.gwt.user.client.Element castElement(Element elem) {
        return (com.google.gwt.user.client.Element) elem;
    }

    /**
     * Add ClickHandler to submitButton. ClickHandler to cancelButton is added automatically
     * @param submitButtonHandler
     */
    public void addClickHandlers(ClickHandler submitButtonHandler) {
        submitBtn.addClickHandler(submitButtonHandler);
        cancelBtn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                toggleReplyBody();
            }
        });
    }

    @Override
    public Widget getWidgetView() {
        return this;
    }

    public Long getMessageToReplyId() {
        return messageToReplyId;
    }

    public void setMessageToReplyId(Long messageToReplyId) {
        this.messageToReplyId = messageToReplyId;
    }



}