/*
 * Copyright (C), eProvement s.r.o. All rights reserved.
 */
package com.eprovement.poptavka.client.homeWelcome;

import com.eprovement.poptavka.client.common.session.Constants;
import com.eprovement.poptavka.client.homeWelcome.interfaces.IHomeWelcomeView;
import com.eprovement.poptavka.client.homeWelcome.interfaces.IHomeWelcomeView.IHomeWelcomePresenter;
import com.eprovement.poptavka.client.homeWelcome.texts.HowItWorks;
import com.eprovement.poptavka.client.homeWelcome.texts.HowItWorks.HowItWorksViews;
import com.eprovement.poptavka.shared.selectors.catLocSelector.ILesserCatLocDetail;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.mvp4g.client.annotation.Presenter;
import com.mvp4g.client.history.NavigationConfirmationInterface;
import com.mvp4g.client.history.NavigationEventCommand;
import com.mvp4g.client.presenter.LazyPresenter;
import java.util.ArrayList;

/**
 * Home Welcome presenter.
 *
 * @author Martin Slavkovsky
 */
@Presenter(view = HomeWelcomeView.class)
public class HomeWelcomePresenter extends LazyPresenter<IHomeWelcomeView, HomeWelcomeEventBus> implements
        IHomeWelcomePresenter, NavigationConfirmationInterface {

    /**************************************************************************/
    /* Attributes                                                             */
    /**************************************************************************/
    private HowItWorks howItWorksDemands;
    private HowItWorks howItWorksSuppliers;

    /**************************************************************************/
    /* General Module events                                                  */
    /**************************************************************************/
    public void onStart() {
        // nothing by default
    }

    /**
     * Sets footer and search bar
     */
    public void onForward() {
        eventBus.setFooter(view.getFooterContainer());
        eventBus.menuStyleChange(Constants.HOME_WELCOME_MODULE);
        eventBus.resetSearchBar(null);
        view.getCategorySelectionModel().clear();
    }

    @Override
    public void confirm(NavigationEventCommand event) {
        // nothing by default
    }

    /**************************************************************************/
    /* Bind evetns                                                            */
    /**************************************************************************/
    /**
     * Bind handlers.
     */
    @Override
    public void bindView() {
        /** ANCHOR. **/
        addSuppliersBtnClickHandler();
        addDemandsBtnClickHandler();
        addHowItWorksSupplierBtnClickHandler();
        addHowItWorksDemandBtnClickHandler();

        /** BUTTONS. **/
        addRegisterSupplierBtnClickHandler();
        addRegisterDemandBtnClickHandler();

        /** OTHERS. **/
        addCategorySelectionModelHandler();
    }

    /**************************************************************************/
    /* Bind events - Helper mehtods                                           */
    /**************************************************************************/
    /** BUTTONS. **/
    private void addSuppliersBtnClickHandler() {
        view.getSuppliersBtn().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                eventBus.goToHomeSuppliersModule(null);
            }
        });
    }

    private void addDemandsBtnClickHandler() {
        view.getDemandsBtn().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                eventBus.goToHomeDemandsModule(null);
            }
        });
    }

    private void addHowItWorksDemandBtnClickHandler() {
        view.getHowItWorksDemandBtn().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onDisplayHowItWorkdsDemands();
            }
        });
    }

    private void addHowItWorksSupplierBtnClickHandler() {
        view.getHowItWorksSupplierBtn().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onDisplayHowItWorkdsSuppliers();
            }
        });
    }

    private void addRegisterSupplierBtnClickHandler() {
        view.getRegisterSupplierBtn().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                eventBus.goToCreateSupplierModule();
            }
        });
    }

    private void addRegisterDemandBtnClickHandler() {
        view.getRegisterDemandBtn().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                eventBus.goToCreateDemandModule();
            }
        });
    }

    /** OTHERS. **/
    private void addCategorySelectionModelHandler() {
        view.getCategorySelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                ILesserCatLocDetail selected =
                        (ILesserCatLocDetail) view.getCategorySelectionModel().getSelectedObject();

                if (selected != null) {
                    int idx = view.getDataProvider().getList().indexOf(selected);
                    eventBus.getICatLocDetail(selected.getId(), idx);
                }
            }
        });
    }

    /**************************************************************************/
    /* Busoness events                                                        */
    /**************************************************************************/
    /**
     * Initialize home welcome module.
     * Request for root categories, sets toolbar.
     */
    public void onGoToHomeWelcomeModule() {
        eventBus.getRootCategories();
        eventBus.setToolbarContent("Home", null);
    }

    /**
     * Methods displays root categories. These categories are divided into cellLists (columns),
     * where number of columns depends on constant: COLUMNS.
     * @param rootCategories - root categories to be displayed
     */
    public void onDisplayCategories(ArrayList<ILesserCatLocDetail> rootCategories) {
        view.displayCategories(rootCategories);
    }

    /**
     * Display HowItWorks widget for demands.
     */
    public void onDisplayHowItWorkdsDemands() {
        eventBus.createCustomToken(HowItWorksViews.HOW_IT_WORKS_DEMAND.getValue());

        if (howItWorksDemands == null) {
            createHowItWorks(HowItWorksViews.HOW_IT_WORKS_DEMAND);
        } else {
            eventBus.setBody(howItWorksDemands);
        }
        eventBus.setToolbarContent("How does it work for Projects", null);
        eventBus.setFooter(howItWorksDemands.getFooterContainer());
    }

    /**
     * Display HowItWorkds widget for suppliers.
     */
    public void onDisplayHowItWorkdsSuppliers() {
        eventBus.createCustomToken(HowItWorksViews.HOW_IT_WORKS_SUPPLIER.getValue());

        if (howItWorksSuppliers == null) {
            createHowItWorks(HowItWorksViews.HOW_IT_WORKS_SUPPLIER);
        } else {
            eventBus.setBody(howItWorksSuppliers);
        }
        eventBus.setToolbarContent("How does it work for Professionals", null);
        eventBus.setFooter(howItWorksSuppliers.getFooterContainer());
    }

    /**************************************************************************/
    /* Helper methods                                                         */
    /**************************************************************************/
    /**
     * Creates how it works instance.
     * @param view enum defining which type of howItWorks widget initialize
     */
    private void createHowItWorks(final HowItWorksViews view) {
        GWT.runAsync(HowItWorks.class, new RunAsyncCallback() {
            @Override
            public void onFailure(Throwable reason) {
                Window.alert("Failed creating How it works for demands.");
            }

            @Override
            public void onSuccess() {
                if (view.equals(HowItWorksViews.HOW_IT_WORKS_DEMAND)) {
                    createHowItWorksDemands();
                } else {
                    createHowItWorksSuppliers();
                }
            }
        });
    }

    /**
     * Creates HowItWorks for demands widget.
     */
    private void createHowItWorksDemands() {
        howItWorksDemands = HowItWorks.createHowItWorksDemand();
        howItWorksDemands.getRegisterBtn().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                eventBus.goToCreateDemandModule();
            }
        });
        eventBus.setBody(howItWorksDemands);
    }

    /**
     * Creates HowItWorks for suppliers widget.
     */
    private void createHowItWorksSuppliers() {
        howItWorksSuppliers = HowItWorks.createHowItWorksSupplier();
        howItWorksSuppliers.getRegisterBtn().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                eventBus.goToCreateSupplierModule();
            }
        });
        eventBus.setBody(howItWorksSuppliers);
    }
}