package com.eprovement.poptavka.client.user.supplierdemands.widgets;

import com.eprovement.poptavka.client.common.session.Constants;
import com.eprovement.poptavka.client.common.session.Storage;
import com.eprovement.poptavka.client.user.widget.grid.UniversalTableWidget;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class SupplierAssignedProjectsView extends Composite
        implements SupplierAssignedProjectsPresenter.SupplierAssignedProjectsLayoutInterface {

    private static SupplierAssignedProjectsLayoutViewUiBinder uiBinder =
            GWT.create(SupplierAssignedProjectsLayoutViewUiBinder.class);

    interface SupplierAssignedProjectsLayoutViewUiBinder extends UiBinder<Widget, SupplierAssignedProjectsView> {
    }
    /**************************************************************************/
    /* DemandContestTable Attrinbutes                                         */
    /**************************************************************************/
    //table definition
    @UiField(provided = true)
    UniversalTableWidget tableWidget;
    /**************************************************************************/
    /* Attrinbutes                                                            */
    /**************************************************************************/
    //table handling buttons
    @UiField
    Button finnishedAssignedProjectButton, replyAssignedProjectButton;
    @UiField(provided = true)
    ListBox actions;
    //detail WrapperPanel
    @UiField
    SimplePanel detailPanel;

    /**************************************************************************/
    /* Initialization                                                            */
    /**************************************************************************/
    @Override
    public void createView() {
        //load custom grid cssStyle
        Storage.RSCS.grid().ensureInjected();

        tableWidget = new UniversalTableWidget(Constants.SUPPLIER_ASSIGNED_PROJECTS);

        actions = new ListBox();
        actions.addItem(Storage.MSGS.action());
        actions.addItem(Storage.MSGS.read());
        actions.addItem(Storage.MSGS.unread());
        actions.addItem(Storage.MSGS.star());
        actions.addItem(Storage.MSGS.unstar());
        actions.setSelectedIndex(0);

        initWidget(uiBinder.createAndBindUi(this));
    }

    /**************************************************************************/
    /* Getters                                                                */
    /**************************************************************************/
    //Table
    @Override
    public UniversalTableWidget getTableWidget() {
        return tableWidget;
    }

    //Buttons
    @Override
    public Button getFinnishedAssignedProjectButton() {
        return finnishedAssignedProjectButton;
    }

    @Override
    public Button getReplyAssignedProjectButton() {
        return replyAssignedProjectButton;
    }

    //ListBox
    @Override
    public ListBox getActions() {
        return actions;
    }

    @Override
    public SimplePanel getDetailPanel() {
        return detailPanel;
    }

    @Override
    public IsWidget getWidgetView() {
        return this;
    }
}