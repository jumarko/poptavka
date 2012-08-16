package com.eprovement.poptavka.client.homedemands;

import com.eprovement.poptavka.client.common.search.SearchModulePresenter;
import com.eprovement.poptavka.client.common.session.Storage;
import com.eprovement.poptavka.domain.enums.DemandTypeType;
import com.eprovement.poptavka.shared.search.FilterItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import java.util.ArrayList;
import java.util.Date;
import com.google.gwt.user.datepicker.client.CalendarUtil;

public class HomeDemandsSearchView extends Composite implements
        SearchModulePresenter.SearchModulesViewInterface {

    private static SearchModulViewUiBinder uiBinder = GWT.create(SearchModulViewUiBinder.class);

    interface SearchModulViewUiBinder extends UiBinder<Widget, HomeDemandsSearchView> {
    }
    @UiField
    TextBox demandTitle, priceFrom, priceTo;
    @UiField
    ListBox demandTypes, creationDate;
    @UiField
    DateBox finnishDateFrom, finnishDateTo;
    @UiField
    Button clearBtn;

    public HomeDemandsSearchView() {
        initWidget(uiBinder.createAndBindUi(this));

        demandTypes.addItem(Storage.MSGS.select());
        for (DemandTypeType type : DemandTypeType.values()) {
            demandTypes.addItem(type.name());
        }
        creationDate.addItem(Storage.MSGS.today());
        creationDate.addItem(Storage.MSGS.yesterday());
        creationDate.addItem(Storage.MSGS.lastWeek());
        creationDate.addItem(Storage.MSGS.lastMonth());
        creationDate.addItem(Storage.MSGS.noLimits());
        creationDate.setSelectedIndex(4);
    }

    @Override
    public ArrayList<FilterItem> getFilter() {
        ArrayList<FilterItem> filters = new ArrayList<FilterItem>();
        if (!demandTitle.getText().equals("")) {
            filters.add(new FilterItem("title", FilterItem.OPERATION_LIKE, demandTitle.getText()));
        }
        if (demandTypes.getSelectedIndex() != 0) {
            filters.add(new FilterItem("type.description", FilterItem.OPERATION_EQUALS,
                    demandTypes.getItemText(demandTypes.getSelectedIndex())));
        }
        if (!priceFrom.getText().equals("")) {
            filters.add(new FilterItem("price", FilterItem.OPERATION_FROM, priceFrom.getText()));
        }
        if (!priceTo.getText().equals("")) {
            filters.add(new FilterItem("price", FilterItem.OPERATION_TO, priceTo.getText()));
        }
        if (creationDate.getSelectedIndex() != 4) {
            filters.add(new FilterItem("createdDate", FilterItem.OPERATION_FROM, getCreatedDate()));
        }
        if (finnishDateFrom.getValue() != null) {
            filters.add(new FilterItem("endDate", FilterItem.OPERATION_FROM, finnishDateFrom.getValue()));
        }
        if (finnishDateTo.getValue() != null) {
            filters.add(new FilterItem("endDate", FilterItem.OPERATION_TO, finnishDateTo.getValue()));
        }
        return filters;
    }

    private Date getCreatedDate() {
        Date date = new Date(); //today -> case 0
        switch (creationDate.getSelectedIndex()) {
            case 1:
                CalendarUtil.addDaysToDate(date, -1);   //yesterday
                break;
            case 2:
                CalendarUtil.addDaysToDate(date, -7);   //last week
                break;
            case 3:
                CalendarUtil.addMonthsToDate(date, -1); //last month
                break;
            default:
                break;
        }
        return date;
    }

    @UiHandler("priceFrom")
    void validatePriceFrom(ChangeEvent event) {
        if (!priceFrom.getText().matches("[0-9]+")) {
            priceFrom.setText("");
        }
    }

    @UiHandler("priceTo")
    void validatePriceTo(ChangeEvent event) {
        if (!priceTo.getText().matches("[0-9]+")) {
            priceTo.setText("");
        }
    }

    @UiHandler("clearBtn")
    void clearBtnAction(ClickEvent event) {
        demandTitle.setText("");
        demandTypes.setSelectedIndex(0);
        priceFrom.setText("");
        priceTo.setText("");
        creationDate.setSelectedIndex(4);
    }

    @Override
    public Widget getWidgetView() {
        return this;
    }
}