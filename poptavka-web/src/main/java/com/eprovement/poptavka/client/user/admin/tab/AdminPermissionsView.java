/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eprovement.poptavka.client.user.admin.tab;

import com.eprovement.poptavka.client.user.widget.grid.UniversalAsyncGrid;
import com.eprovement.poptavka.client.user.widget.grid.UniversalAsyncGrid.GetValue;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ProvidesKey;

import com.eprovement.poptavka.shared.domain.adminModule.PermissionDetail;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Martin Slavkovsky
 */
public class AdminPermissionsView extends Composite implements AdminPermissionsPresenter.AdminPermissionsInterface {

    private static AdminDemandsViewUiBinder uiBinder = GWT.create(AdminDemandsViewUiBinder.class);

    interface AdminDemandsViewUiBinder extends UiBinder<Widget, AdminPermissionsView> {
    }
    //*************************************************************************/
    //                              ATTRIBUTES                                */
    //*************************************************************************/
    @UiField
    Button commit, rollback, refresh;
    @UiField
    Label changesLabel;
    // PAGER
    @UiField(provided = true)
    SimplePager pager;
    @UiField(provided = true)
    ListBox pageSizeCombo;
    // TABLE
    @UiField(provided = true)
    UniversalAsyncGrid<PermissionDetail> dataGrid;
    // Editable Columns
    private Column<PermissionDetail, String> descriptionColumn;
    private Column<PermissionDetail, String> nameColumn;
    private List<String> gridColumns = Arrays.asList(
            new String[]{
                "id", "code", "name", "description"
            });
    // The key provider that provides the unique ID of a PermissionDetail.
    private static final ProvidesKey<PermissionDetail> KEY_PROVIDER = new ProvidesKey<PermissionDetail>() {

        @Override
        public Object getKey(PermissionDetail item) {
            return item == null ? null : item.getId();
        }
    };

    //*************************************************************************/
    //                          INITIALIZATOIN                                */
    //*************************************************************************///
    /**
     * creates WIDGET view.
     */
    @Override
    public void createView() {
        pageSizeCombo = new ListBox();
        pageSizeCombo.addItem("10");
        pageSizeCombo.addItem("15");
        pageSizeCombo.addItem("20");
        pageSizeCombo.addItem("25");
        pageSizeCombo.addItem("30");
        pageSizeCombo.setSelectedIndex(1);
        initDataGrid();
        initWidget(uiBinder.createAndBindUi(this));
        changesLabel.setText("0");
    }

    /**
     * Creates table with accessories - columns, pager, selection model.
     */
    private void initDataGrid() {
        GWT.log("init AdminPermissions DataGrid initialized");

        // TABLE
        dataGrid = new UniversalAsyncGrid<PermissionDetail>(KEY_PROVIDER, gridColumns);
        dataGrid.setPageSize(this.getPageSize());
        dataGrid.setWidth("700px");
        dataGrid.setHeight("500px");
        dataGrid.setEmptyTableWidget(new Label("No data available."));

        // PAGER
        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
        pager.setDisplay(dataGrid);

        // COLUMNS
        initTableColumns();
    }

    /**
     * Add the columns to the table.
     */
    private void initTableColumns() {

        // ID
        dataGrid.addColumn(new TextCell(), "ID", true, 50,
                new GetValue<String>() {

                    @Override
                    public String getValue(Object object) {
                        return String.valueOf(((PermissionDetail) object).getId());
                    }
                });

        // Code
        dataGrid.addColumn(new TextCell(), "Code", true, 50,
                new GetValue<String>() {

                    @Override
                    public String getValue(Object object) {
                        return ((PermissionDetail) object).getCode();
                    }
                });

        // name
        nameColumn = dataGrid.addColumn(new EditTextCell(), "Name", true, 100,
                new GetValue<String>() {

                    @Override
                    public String getValue(Object object) {
                        return ((PermissionDetail) object).getName();
                    }
                });

        // description
        descriptionColumn = dataGrid.addColumn(new EditTextCell(), "Description", true, 100,
                new GetValue<String>() {

                    @Override
                    public String getValue(Object object) {
                        return ((PermissionDetail) object).getDescription();
                    }
                });
    }

    //*************************************************************************/
    //                      GETTER METHODS (defined by interface)             */
    //*************************************************************************/
    //                          *** TABLE ***
    /**
     * @return TABLE (DataGrid)
     */
    @Override
    public UniversalAsyncGrid<PermissionDetail> getDataGrid() {
        return dataGrid;
    }

    /**
     * @return table column: DESCRIPTION
     */
    @Override
    public Column<PermissionDetail, String> getDescriptionColumn() {
        return descriptionColumn;
    }

    /**
     * @return table column: NAME
     */
    @Override
    public Column<PermissionDetail, String> getNameColumn() {
        return nameColumn;
    }

    //                         *** PAGER ***
    /*
     * @return pager
     */
    @Override
    public SimplePager getPager() {
        return pager;
    }

    /**
     * @return table/pager size: COMBO
     */
    @Override
    public ListBox getPageSizeCombo() {
        return pageSizeCombo;
    }

    /**
     * @return table/pager size: VALUE
     */
    @Override
    public int getPageSize() {
        return Integer.valueOf(pageSizeCombo.getItemText(pageSizeCombo.getSelectedIndex()));
    }

    //                          *** BUTTONS ***
    /**
     * @return COMMIT button
     */
    @Override
    public Button getCommitBtn() {
        return commit;
    }

    /**
     * @return ROLLBACK button
     */
    @Override
    public Button getRollbackBtn() {
        return rollback;
    }

    /**
     * @return REFRESH button
     */
    @Override
    public Button getRefreshBtn() {
        return refresh;
    }

    //                          *** OTHER ***
    /**
     * @return label for displaying informations for user
     */
    @Override
    public Label getChangesLabel() {
        return changesLabel;
    }

    /**
     * @return this widget as it is
     */
    @Override
    public Widget getWidgetView() {
        return this;
    }
}