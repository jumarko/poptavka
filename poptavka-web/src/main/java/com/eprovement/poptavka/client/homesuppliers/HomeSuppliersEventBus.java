/*
 * HomeSuppliersEventBus servers all events for module HomeSuppliersModule.
 *
 * Specification:
 * Wireframe: http://www.webgres.cz/axure/ -> VR Vypis dodavatelov
 */
package com.eprovement.poptavka.client.homesuppliers;

import com.google.gwt.user.client.ui.IsWidget;
import com.mvp4g.client.annotation.Debug;
import com.mvp4g.client.annotation.Event;
import com.mvp4g.client.annotation.Events;
import com.mvp4g.client.annotation.Forward;
import com.mvp4g.client.annotation.Start;
import com.mvp4g.client.event.EventBus;
import com.eprovement.poptavka.client.main.common.search.SearchModuleDataHolder;
import com.eprovement.poptavka.domain.enums.OrderType;
import com.eprovement.poptavka.shared.domain.CategoryDetail;
import com.eprovement.poptavka.shared.domain.supplier.FullSupplierDetail;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author martin.slavkovsky
 */
@Events(startView = HomeSuppliersView.class, module = HomeSuppliersModule.class)
@Debug(logLevel = Debug.LogLevel.DETAILED)
public interface HomeSuppliersEventBus extends EventBus {

    /**
     * Start event is called only when module is instantiated first time.
     * We can use it for history initialization.
     */
    @Start
    @Event(handlers = HomeSuppliersPresenter.class)
    void start();

    /**
     * Forward event is called only if it is configured here. It there is
     * nothing to carry out in this method we should remove forward event to
     * save the number of method invocations.
     */
    @Forward
    @Event(handlers = HomeSuppliersPresenter.class)
    void forward();

    /**************************************************************************/
    /* Navigation events.                                                     */
    /**************************************************************************/
    /**
     * The only entry point to this module due to code-splitting and exclusive
     * fragment.
     */
    @Event(handlers = HomeSuppliersPresenter.class)
    void goToHomeSuppliersModule(SearchModuleDataHolder searchDataHolder);

    @Event(handlers = HomeSuppliersPresenter.class)
    void displayParentOrChild(SearchModuleDataHolder searchDataHolder);
    /**************************************************************************/
    /* Parent events                                                          */
    /**************************************************************************/
    // TODO Praso - GENERAL PARENT EVENTS WILL BE LATER SEPARATED WITHIN BASECHILDEVENTBUS TO SAVE CODE
    @Event(forwardToParent = true)
    void loadingShow(String loadingMessage);

    @Event(forwardToParent = true)
    void loadingHide();

    @Event(forwardToParent = true)
    void setUpSearchBar(IsWidget searchView, boolean cat, boolean loc, boolean advBtn);

    @Event(forwardToParent = true)
    void menuStyleChange(int loadedModule);

    @Event(forwardToParent = true)
    void userMenuStyleChange(int loadedModule);
    /**************************************************************************/
    /* Business events handled by Presenters.                                 */
    /**************************************************************************/
    /**************************************************************************/
    /* Business events handled by Presenters - Display events                 */
    /**************************************************************************/
    // ROOT CATEGORIES
    @Event(handlers = HomeSuppliersPresenter.class)
    void displayRootcategories(ArrayList<CategoryDetail> list);

    // SUB CATEGORIES
    @Event(handlers = HomeSuppliersPresenter.class)
    void displaySubCategories(ArrayList<CategoryDetail> list, Long parentCategory);

    // SUPPLIERS
    @Event(handlers = HomeSuppliersPresenter.class)
    void displaySuppliers(List<FullSupplierDetail> list);

    /**
     * Display sub-categories, suppliers of selected category and detail of
     * selected supplier.
     */
    // CHILD WIDGET
    @Event(handlers = HomeSuppliersPresenter.class)
    void displayChildWidget(Long id);

    /**************************************************************************/
    /* Business events handled by Presenters - Path events                    */
    /**************************************************************************/
    // UPDATE
    @Event(handlers = HomeSuppliersPresenter.class)
    void updatePath(ArrayList<CategoryDetail> categories);

    /**
     * After retrieving category, add its name to path.
     * @param categoryDetail
     */
    @Event(handlers = HomeSuppliersPresenter.class, historyConverter = HomeSuppliersHistoryConverter.class)
    String addToPath(CategoryDetail categoryDetail);

    /**************************************************************************/
    /* Business events handled by Presenters - Data events                    */
    /**************************************************************************/
    // PROVIDER
    @Event(handlers = HomeSuppliersPresenter.class)
    void createAsyncDataProvider(final int totalFound);

    /**************************************************************************/
    /* Business events handled by Handlers.                                   */
    /**************************************************************************/
    // CATEGORIES
    @Event(handlers = HomeSuppliersHandler.class)
    void getCategories();

    @Event(handlers = HomeSuppliersHandler.class)
    void getCategoryParents(Long categoryId);

    @Event(handlers = HomeSuppliersHandler.class)
    void getSubCategories(Long category);

    // SUPPLIERS
    @Event(handlers = HomeSuppliersHandler.class)
    void getSuppliersCount(SearchModuleDataHolder detail);

    @Event(handlers = HomeSuppliersHandler.class)
    void getSuppliers(int start, int count, SearchModuleDataHolder detail, Map<String, OrderType> orderColumns);

}
