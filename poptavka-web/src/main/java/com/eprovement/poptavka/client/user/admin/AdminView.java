package com.eprovement.poptavka.client.user.admin;

import com.eprovement.poptavka.client.common.OverflowComposite;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.eprovement.poptavka.resources.StyleResource;

public class AdminView extends OverflowComposite implements AdminPresenter.AdminModuleInterface {

    private static AdminModuleViewUiBinder uiBinder = GWT.create(AdminModuleViewUiBinder.class);

    interface AdminModuleViewUiBinder extends UiBinder<Widget, AdminView> {
    }
    private static final Logger LOGGER = Logger.getLogger(AdminView.class.getName());
    @UiField
    SimplePanel contentPanel;
    @UiField
    Button demandsButton, offersButton, clientsButton, suppliersButton,
    accessRolesButton, emailActivationsButton, invoicesButton, messagesButton,
    paymentMethodsButton, permissionsButton, preferencesButton, problemsButton; //ourPaymentDetailsButton,

    @Override
    public void createView() {
        StyleResource.INSTANCE.common().ensureInjected();
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public Widget getWidgetView() {
        return this;
    }

    @Override
    public void setContent(Widget contentWidget) {
        contentPanel.setWidget(contentWidget);
    }

    @Override
    public Button getDemandsButton() {
        return demandsButton;
    }

    @Override
    public Button getClientsButton() {
        return clientsButton;
    }

    @Override
    public Button getOffersButton() {
        return offersButton;
    }

    @Override
    public Button getSuppliersButton() {
        return suppliersButton;
    }

    @Override
    public Button getAccessRoleButton() {
        return accessRolesButton;
    }

    @Override
    public Button getEmailActivationButton() {
        return emailActivationsButton;
    }

    @Override
    public Button getInvoiceButton() {
        return invoicesButton;
    }

    @Override
    public Button getMessageButton() {
        return messagesButton;
    }

//    @Override
//    public Button getOurPaymentDetailsButton() {
//        ourPaymentDetailsButton.getTargetHistoryButton(ButtonString);
//    }

    @Override
    public Button getPaymentMethodButton() {
        return paymentMethodsButton;
    }

    @Override
    public Button getPermissionButton() {
        return permissionsButton;
    }

    @Override
    public Button getPreferenceButton() {
        return preferencesButton;
    }

    @Override
    public Button getProblemButton() {
        return problemsButton;
    }

    @Override
    public SimplePanel getContentPanel() {
        return contentPanel;
    }
}
