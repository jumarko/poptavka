/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eprovement.poptavka.client.user.clientdemands.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 * @author Martin Slavkovsky
 */
public class ClientDemandsWelcomeView extends Composite
        implements ClientDemandsWelcomePresenter.ClientDemandsWelcomeViewInterface {

    private static ClientDemandsWelcomeViewUiBinder uiBinder = GWT.create(ClientDemandsWelcomeViewUiBinder.class);

    interface ClientDemandsWelcomeViewUiBinder extends UiBinder<Widget, ClientDemandsWelcomeView> {
    }

    @UiField HTML myDemandsUnreadMessages, offeredDemandsUnreadMessages, assignedDemandsUnreadMessages,
    closedDemandsUnreadMessages;

    /**
     * creates WIDGET view
     */
    @Override
    public void createView() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    /**
     * @return this widget as it is
     */
    @Override
    public Widget getWidgetView() {
        return this;
    }

    /**
     * @return the myDemandsUnreadMessages
     */
    public HTML getMyDemandsUnreadMessages() {
        return myDemandsUnreadMessages;
    }

    /**
     * @return the offeredDemandsUnreadMessages
     */
    public HTML getOfferedDemandsUnreadMessages() {
        return offeredDemandsUnreadMessages;
    }

    /**
     * @return the assignedDemandsUnreadMessages
     */
    public HTML getAssignedDemandsUnreadMessages() {
        return assignedDemandsUnreadMessages;
    }

    /**
     * @return the closedDemandsUnreadMessages
     */
    public HTML getClosedDemandsUnreadMessages() {
        return closedDemandsUnreadMessages;
    }
}