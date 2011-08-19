package cz.poptavka.sample.client.user;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.mvp4g.client.annotation.Debug;
import com.mvp4g.client.annotation.Event;
import com.mvp4g.client.annotation.Events;
import com.mvp4g.client.event.EventBusWithLookup;
import cz.poptavka.sample.client.user.admin.AdminHistoryConverter;

import cz.poptavka.sample.client.user.admin.AdminLayoutPresenter;
import cz.poptavka.sample.client.user.admin.tab.AdminDemandInfoPresenter;
import cz.poptavka.sample.client.user.admin.tab.AdminOfferInfoPresenter;
import cz.poptavka.sample.client.user.admin.tab.AdminOffersPresenter;
import cz.poptavka.sample.client.user.admin.tab.AdminSuppliersPresenter;
import cz.poptavka.sample.client.user.admin.tab.AdminDemandsPresenter;
import cz.poptavka.sample.client.user.admin.tab.DemandsOperatorPresenter;
import cz.poptavka.sample.client.user.demands.DemandsHistoryConverter;
import cz.poptavka.sample.client.user.demands.DemandsLayoutPresenter;
import cz.poptavka.sample.client.user.demands.tab.AllDemandsPresenter;
import cz.poptavka.sample.client.user.demands.tab.AllSuppliersPresenter;
import cz.poptavka.sample.client.user.demands.tab.MyDemandsPresenter;
import cz.poptavka.sample.client.user.demands.tab.NewDemandPresenter;
import cz.poptavka.sample.client.user.demands.tab.OffersPresenter;
import cz.poptavka.sample.client.user.demands.tab.PotentialDemandsPresenter;
import cz.poptavka.sample.client.user.demands.widget.DetailWrapperPresenter;
import cz.poptavka.sample.client.user.handler.AllDemandsHandler;
import cz.poptavka.sample.client.user.handler.AllSuppliersHandler;
import cz.poptavka.sample.client.user.handler.MessageHandler;
import cz.poptavka.sample.client.user.handler.UserHandler;
import cz.poptavka.sample.client.user.problems.MyProblemsHistoryConverter;
import cz.poptavka.sample.client.user.problems.MyProblemsPresenter;
import cz.poptavka.sample.shared.domain.CategoryDetail;
import cz.poptavka.sample.shared.domain.DemandDetailForDisplayDemands;
import cz.poptavka.sample.shared.domain.LocalityDetail;
import cz.poptavka.sample.shared.domain.OfferDetail;
import cz.poptavka.sample.shared.domain.UserDetail;
import cz.poptavka.sample.shared.domain.demand.BaseDemandDetail;
import cz.poptavka.sample.shared.domain.demand.DemandDetail;
import cz.poptavka.sample.shared.domain.demand.FullDemandDetail;
import cz.poptavka.sample.shared.domain.message.ClientDemandMessageDetail;
import cz.poptavka.sample.shared.domain.message.MessageDetailImpl;
import cz.poptavka.sample.shared.domain.message.OfferDemandMessageImpl;
import cz.poptavka.sample.shared.domain.message.OfferMessageDetailImpl;
import cz.poptavka.sample.shared.domain.message.PotentialDemandMessageImpl;
import cz.poptavka.sample.shared.domain.offer.FullOfferDetail;
import cz.poptavka.sample.shared.domain.type.ViewType;
import java.util.Collection;

@Events(startView = UserView.class, module = UserModule.class)
@Debug(logLevel = Debug.LogLevel.DETAILED)
public interface UserEventBus extends EventBusWithLookup {

    /** init method. **/
    @Event(handlers = { UserPresenter.class }, historyConverter = UserHistoryConverter.class)
    String atAccount();

    /** getter for UserDetail. **/
    @Event(handlers = UserHandler.class)
    void getUser();

    /**
     * setter of userDetail for whole application. This user represents logged user
     *
     * @param user
     */
    @Event(handlers = UserPresenter.class)
    void setUser(UserDetail user);

    /** Client demands list GETTER/SETTER **/
    @Event(handlers = UserPresenter.class)
    void requestClientDemands();

    /** Potential demands GETTER/SETTER. **/
    @Event(handlers = UserPresenter.class)
    void requestPotentialDemands();
    @Event(handlers = MessageHandler.class)
    void getPotentialDemands(long businessUserId);
    @Event(handlers = PotentialDemandsPresenter.class)
    void responsePotentialDemands(ArrayList<PotentialDemandMessageImpl> potentialDemandsList);

    /** Offer demands GETTER/SETTER. **/
    // this same method could be called to MyDemandsPresenter
    // depends on it's demandDetailType and what will be difference in data, probably different
    // method will be used
    @Event(handlers = UserPresenter.class)
    void requestClientOfferDemands();
    @Event(handlers = MessageHandler.class)
    void getClientDemandsWithOffers(Long businessUserId);
    @Event(handlers = OffersPresenter.class)
    void responseClientDemandsWithOffers(ArrayList<OfferDemandMessageImpl> result);

    /** Demand Offers GETTER/SETTER. **/
    @Event(handlers = UserHandler.class)
    void getDemandOffers(long demandId, long threadRootId);
    @Event(handlers = OffersPresenter.class)
    void setDemandOffers(ArrayList<OfferDetail> offers);

    /**
     * For switching between main tabs like Demands | Messages | Settings | etc.
     *
     * @param tabBody
     *            current widget
     */
    @Event(handlers = UserPresenter.class)
    void setTabWidget(Widget tabBody);

    @Event(handlers = UserPresenter.class)
    void setTabAdminWidget(Widget tabBody);

    // for operator only
//    @Event
//    void invokeMyProblems();

    /**
     * Handlers for widget MyProblems.
     */
    @Event(handlers = MyProblemsPresenter.class)
    void requestMyProblems();

    /** handler method area. **/

    /** REQUESTs && RESPONSEs **/
    // Always in pairs
    /** get client id. **/
    @Event(handlers = UserPresenter.class)
    void requestClientId(FullDemandDetail newDemand);
//
//    @Event(handlers = { MyDemandsPresenter.class, DemandsOperatorPresenter.class }, passive = true)
//    void responseClientDemands(ArrayList<FullDemandDetail> demands);

    /**
     * common method for displaying Demand Detail in the window.
     * **/
    // TODO implements demandDetail section loading for Wrapper
    @Event(handlers = { UserHandler.class,
            // serves for visual sing, that content is loading
            DetailWrapperPresenter.class })
    void getDemandDetail(Long demandId, ViewType typeOfDetail);

    @Event(handlers = DetailWrapperPresenter.class, passive = true)
    void setFullDemandDetail(FullDemandDetail detail);

    @Event(handlers = DetailWrapperPresenter.class, passive = true)
    void setBaseDemandDetail(BaseDemandDetail detail);

    /** method for displaying conversation to selected demand. **/
    @Event(handlers = {UserPresenter.class, DetailWrapperPresenter.class })
    void requestPotentialDemandConversation(long messageId, long userMessageId);

    @Event(handlers = MessageHandler.class)
    void getPotentialDemandConversation(long messageId, long userId, long userMessageId);

    @Event(handlers = DetailWrapperPresenter.class, passive = true)
    void setPotentialDemandConversation(ArrayList<MessageDetailImpl> messageList, ViewType wrapperhandlerType);

    @Event(handlers = DetailWrapperPresenter.class, passive = true)
    void setSingleDemandConversation(ArrayList<MessageDetailImpl> messageList);

    /**
     * Bubbling message to send to UserPresenter to get the user ID.
     *
     * @param messageToSend
     *            message to be sent
     */
    @Event(handlers = UserPresenter.class)
    void bubbleMessageSending(MessageDetailImpl messageToSend, ViewType viewType);

    @Event(handlers = MessageHandler.class)
    void sendMessageToPotentialDemand(MessageDetailImpl messageToSend, ViewType viewType);

    @Event(handlers = DetailWrapperPresenter.class)
    void addMessageToPotentailDemandConversation(MessageDetailImpl result, ViewType wrapperhandlerType);

    /** Offers message display & state change. **/
    @Event(handlers = DetailWrapperPresenter.class)
    void setOfferMessage(OfferDetail offerDetail);

    @Event(handlers = MessageHandler.class)
    void getOfferStatusChange(OfferDetail offerDetail);

    @Event(handlers = OffersPresenter.class)
    void setOfferDetailChange(OfferDetail offerDetail);

    /**
     * Bubbling offer to send to UserPresenter to get the user ID and supplier ID.
     *
     * @param messageToSend
     *            message to be sent
     */
    @Event(handlers = UserPresenter.class)
    void bubbleOfferSending(OfferMessageDetailImpl offerToSend);

    @Event(handlers = MessageHandler.class)
    void sendDemandOffer(OfferMessageDetailImpl offerToSend);

    @Event(handlers = MessageHandler.class)
    void requestPotentialDemandReadStatusChange(ArrayList<Long> messages, boolean isRead);

    @Event(handlers = AdminDemandsPresenter.class)
    void responseAdminOfferDetail(Widget widget);

    /** Call to UserPresenter **/
    /**
     * Display User View - parent Widget for client-logged user section.
     *
     * @param body
     */
    @Event(forwardToParent = true)
    void setBodyHolderWidget(Widget body);

    /** display child widget. **/
    @Event(handlers = DemandsLayoutPresenter.class)
    void displayContent(Widget contentWidget);

    @Event(handlers = AdminLayoutPresenter.class)
    void displayAdminContent(Widget contentWidget);

    /**********************************************************************************************
     ************ Navigation events section. /* Presenters do NOT listen to events when deactivated
     **********************************************************************************************
     *     ----------------------- DEMANDS SECTION ----------------------------      */

    @Event(handlers = MyDemandsPresenter.class, activate = MyDemandsPresenter.class, deactivate = {
            OffersPresenter.class, NewDemandPresenter.class, PotentialDemandsPresenter.class,
            DemandsOperatorPresenter.class, AdminDemandsPresenter.class, AdminSuppliersPresenter.class,
            AdminOffersPresenter.class, AllDemandsPresenter.class, AllSuppliersPresenter.class },
            historyConverter = DemandsHistoryConverter.class)
    String invokeMyDemands();

//    @Event(handlers = MyProblemsPresenter.class, activate = MyProblemsPresenter.class, deactivate = {
//            OffersPresenter.class, NewDemandPresenter.class, PotentialDemandsPresenter.class,
//            DemandsOperatorPresenter.class, AdminDemandsPresenter.class },
//            historyConverter = DemandsHistoryConverter.class)
    @Event(handlers = MyProblemsPresenter.class, historyConverter = MyProblemsHistoryConverter.class)
    String invokeMyProblems();

    @Event(handlers = OffersPresenter.class, activate = OffersPresenter.class, deactivate = {
            MyDemandsPresenter.class, NewDemandPresenter.class, PotentialDemandsPresenter.class,
            DemandsOperatorPresenter.class, AdminDemandsPresenter.class, AdminOffersPresenter.class,
            AllDemandsPresenter.class, AllSuppliersPresenter.class, AdminSuppliersPresenter.class },
            historyConverter = DemandsHistoryConverter.class)
    String invokeOffers();

    @Event(handlers = NewDemandPresenter.class, activate = NewDemandPresenter.class, deactivate = {
            OffersPresenter.class, MyDemandsPresenter.class, PotentialDemandsPresenter.class,
            DemandsOperatorPresenter.class, AdminDemandsPresenter.class, AdminOffersPresenter.class,
            AllDemandsPresenter.class, AllSuppliersPresenter.class, AdminSuppliersPresenter.class },
            historyConverter = DemandsHistoryConverter.class)
    String invokeNewDemand();

    @Event(handlers = PotentialDemandsPresenter.class, activate = PotentialDemandsPresenter.class, deactivate = {
            OffersPresenter.class, MyDemandsPresenter.class, NewDemandPresenter.class,
            DemandsOperatorPresenter.class, AdminDemandsPresenter.class, AdminOffersPresenter.class,
            AdminSuppliersPresenter.class, AllDemandsPresenter.class, AllSuppliersPresenter.class },
            historyConverter = DemandsHistoryConverter.class)
    String invokePotentialDemands();

    @Event(handlers = DemandsOperatorPresenter.class, activate = DemandsOperatorPresenter.class, deactivate = {
            OffersPresenter.class, MyDemandsPresenter.class, PotentialDemandsPresenter.class,
            NewDemandPresenter.class, AdminDemandsPresenter.class, AdminOffersPresenter.class,
            AllDemandsPresenter.class, AllSuppliersPresenter.class, AdminSuppliersPresenter.class },
            historyConverter = DemandsHistoryConverter.class)
    String invokeDemandsOperator();

    @Event(handlers = AllDemandsPresenter.class, activate = AllDemandsPresenter.class, deactivate = {
            OffersPresenter.class, MyDemandsPresenter.class, PotentialDemandsPresenter.class,
            NewDemandPresenter.class, DemandsOperatorPresenter.class, AdminDemandsPresenter.class,
            AllSuppliersPresenter.class, AdminOffersPresenter.class, AdminSuppliersPresenter.class },
            historyConverter = DemandsHistoryConverter.class)
    String invokeAtDemands();

    @Event(handlers = AllSuppliersPresenter.class, activate = AllSuppliersPresenter.class, deactivate = {
            OffersPresenter.class, MyDemandsPresenter.class, PotentialDemandsPresenter.class,
            NewDemandPresenter.class, DemandsOperatorPresenter.class, AdminDemandsPresenter.class,
            AllDemandsPresenter.class, AdminOffersPresenter.class, AdminSuppliersPresenter.class },
            historyConverter = DemandsHistoryConverter.class)
    String invokeAtSuppliers();

     /*      ------------------------ ADMINISTRATION SECTION ----------------------------    */

//    @Event(handlers = AdminDemandsPresenter.class, historyConverter = AdminHistoryConverter.class)
    @Event(handlers = AdminDemandsPresenter.class, activate = AdminDemandsPresenter.class, deactivate = {
            OffersPresenter.class, MyDemandsPresenter.class, PotentialDemandsPresenter.class,
            NewDemandPresenter.class, DemandsOperatorPresenter.class, AdminSuppliersPresenter.class,
            AllDemandsPresenter.class, AdminOffersPresenter.class, AllSuppliersPresenter.class },
            historyConverter = AdminHistoryConverter.class)
    String invokeAdminDemands();

    @Event(handlers = AdminOffersPresenter.class, historyConverter = AdminHistoryConverter.class)
    String invokeAdminOffers();

//    @Event(handlers = AdminSuppliersPresenter.class, historyConverter = AdminHistoryConverter.class)
    @Event(handlers = AdminSuppliersPresenter.class, activate = AdminSuppliersPresenter.class, deactivate = {
            OffersPresenter.class, MyDemandsPresenter.class, PotentialDemandsPresenter.class,
            NewDemandPresenter.class, DemandsOperatorPresenter.class, AdminDemandsPresenter.class,
            AllDemandsPresenter.class, AdminOffersPresenter.class, AllSuppliersPresenter.class },
            historyConverter = AdminHistoryConverter.class)
    String invokeAdminSuppliers();

    //TODO Martin - dorobit ?
//    @Event(handlers = AllSuppliersPresenter.class, historyConverter = AdminHistoryConverter.class)
//    String invokeAdminUsers();

    /***********************************************************************************************
     ************************* Navigation Events section END ***************************************
     **********************************************************************************************/

    /*
     * hacky later fire event Needed when refreshing in User Section - refresh not neededi in prod.
     */

    /***********************************************************************************************
     ***********************  USER SECTION (THE BASE) **********************************************
     **********************************************************************************************/
    @Event(handlers = UserPresenter.class)
    void fireMarkedEvent();

    /** marks event to load right after main UI is displayed. */
    // devel method
    @Event(handlers = UserPresenter.class)
    void markEventToLoad(String historyName);

    /** Calls to MainEventBus. **/
    @Event(forwardToParent = true)
    void initDemandBasicForm(SimplePanel holderPanel);

    @Event(forwardToParent = true)
    void initCategoryWidget(SimplePanel holderPanel);

    @Event(forwardToParent = true)
    void initLocalityWidget(SimplePanel holderPanel);

    @Event(forwardToParent = true)
    void initDemandAdvForm(SimplePanel holderPanel);

    @Event(forwardToParent = true)
    void loadingShow(String progressGettingDemandData);

    @Event(forwardToParent = true)
    void loadingShowWithAnchor(String progressGettingDemandDataring, Widget anchor);

    @Event(forwardToParent = true)
    void createDemand(FullDemandDetail demand, Long id);

    @Event(forwardToParent = true)
    void setUserLayout();

    @Event(forwardToParent = true)
    void loadingHide();

    @Event(handlers = UserPresenter.class)
    void setUserInteface(StyleInterface widgetView);

    @Event(handlers = UserPresenter.class)
    void clearUserOnUnload();

    /** TEST CLIENT DEMANDS ON MESSAGE BASED SYSTEM **/
    // START
    @Event(handlers = MessageHandler.class)
    void getClientDemands(Long userId, int fakeParameter);

    // Beho: ??? needed ???
//    @Event(handlers = MyDemandsPresenter.class)
//    void responseClientDemands(ArrayList<MessageDetail> result);

    @Event(handlers = { DemandsLayoutPresenter.class, AdminLayoutPresenter.class })
    void toggleLoading();

    @Event(handlers = UserPresenter.class)
    void requestDemandsWithConversationInfo();

    @Event(handlers = MessageHandler.class)
    void getClientDemandWithConversations(Long userId, Long clientId);

    @Event(handlers = MyDemandsPresenter.class)
    void setClientDemandWithConversations(ArrayList<ClientDemandMessageDetail> result);

    @Event(handlers = MessageHandler.class)
    void requestDemandConversations(long messageId);

    @Event(handlers = MyDemandsPresenter.class)
    void setDemandConversations(ArrayList<MessageDetailImpl> conversations);

    @Event(handlers = {MessageHandler.class, DetailWrapperPresenter.class })
    void requestSingleConversation(long threadRootId, long messageId);
    //END

    @Event(handlers = AdminLayoutPresenter.class)
    void initAdmin();

    /**********************************************************************************************
     ***********************  DEMANDS SECTION *****************************************************
     **********************************************************************************************/

    /* ---------------- ALL SUPPLIERS ----------------->>>>>>> */
    //Category
    @Event(handlers = AllSuppliersHandler.class)
    void getSubCategories(Long category);

    @Event(handlers = {AllDemandsHandler.class, AllSuppliersHandler.class })
    void getCategories();

    //Locality
    @Event(handlers = {AllDemandsHandler.class, AllSuppliersHandler.class })
    void getLocalities();

    //Suppliers
//    @Event(handlers = RootPresenter.class)
//    void getSuppliers(Long category, Long locality);

    @Event(handlers = AllSuppliersHandler.class)
    void getSuppliersByCategoryLocality(int start, int count, Long category, String locality);

    @Event(handlers = AllSuppliersHandler.class)
    void getSuppliersByCategory(int start, int count, Long category);

    @Event(handlers = AllSuppliersHandler.class)
    void getSuppliersCount(Long category, String locality);

    @Event(handlers = AllSuppliersHandler.class)
    void getSuppliersCountByCategory(Long category);

    //Display
    @Event(handlers = AllSuppliersPresenter.class)
    void atDisplaySuppliers(Long categoryID);

    @Event(handlers = AllSuppliersPresenter.class)
    void displayRootcategories(ArrayList<CategoryDetail> list);

    @Event(handlers = AllSuppliersPresenter.class)
    void displaySubCategories(ArrayList<CategoryDetail> list, Long parentCategory);

    @Event(handlers = AllSuppliersPresenter.class)
    void displaySuppliers(ArrayList<UserDetail> list);

//    @Event(handlers = {AllSuppliersPresenter.class, DemandsPresenter.class })
    @Event(handlers = AllSuppliersPresenter.class)
    void setLocalityData(ArrayList<LocalityDetail> list);

    @Event(handlers = AllSuppliersPresenter.class, historyConverter = DemandsHistoryConverter.class)
    void addToPath(CategoryDetail category);

    @Event(handlers = AllSuppliersPresenter.class)
    void removeFromPath(Long code);

    @Event(handlers = AllSuppliersPresenter.class)
    void setCategoryID(Long categoryCode);

    @Event(handlers = AllSuppliersPresenter.class)
    void createAsyncDataProviderSupplier(final long totalFound);
    /* <<<<<<<<<<-------- ALL SUPPLIERS -------------------- */

    /* ------------------ ALL DEMANDS ---------------------->>>>  */
    //Demand
    @Event(handlers = AllDemandsHandler.class)
    void getDemands(int fromResult, int toResult);

    @Event(handlers = AllDemandsHandler.class)
    void getAllDemandsCount();

    @Event(handlers = AllDemandsHandler.class)
    void getDemandsCountCategory(long id);

    @Event(handlers = AllDemandsHandler.class)
    void getDemandsCountLocality(String code);

    @Event(handlers = AllDemandsHandler.class)
    void getDemandsByCategories(int fromResult, int toResult, long id);

    @Event(handlers = AllDemandsHandler.class)
    void getDemandsByLocalities(int fromResult, int toResult, String id);

    //Display
    @Event(handlers = AllDemandsPresenter.class)
    void setCategoryData(ArrayList<CategoryDetail> list);

    @Event(handlers = AllDemandsPresenter.class)
    void displayDemands(Collection<DemandDetailForDisplayDemands> result);

    @Event(handlers = AllDemandsPresenter.class)
    void setDemand(DemandDetailForDisplayDemands demand);

    @Event(handlers = AllDemandsPresenter.class)
    void createAsyncDataProvider();

    @Event(handlers = AllDemandsPresenter.class)
    void setResultSource(String resultSource);

    @Event(handlers = AllDemandsPresenter.class)
    void setResultCount(long resultCount);
    /* <<<<<<<<<<-------- ALL DEMANDS -------------------- */

    /**********************************************************************************************
     ***********************  MESSAGES SECTION ****************************************************
     **********************************************************************************************/


    /**********************************************************************************************
     ***********************  SETTINGS SECTION ****************************************************
     **********************************************************************************************/

    /**********************************************************************************************
     ***********************  CONTACTS SECTION ****************************************************
     **********************************************************************************************/

    /**********************************************************************************************
     ***********************  ADMIN SECTION *******************************************************
     **********************************************************************************************/
    /* ----------------- ADMIN DEMANDS -------------------->>>>>>>>> */
    @Event(handlers = UserHandler.class)
    void getAllDemands();

    @Event(handlers = UserHandler.class)
    void updateDemand(FullDemandDetail demand);

    @Event(handlers = UserHandler.class)
    void updateOffer(FullOfferDetail offer);

    @Event(handlers = AdminDemandsPresenter.class)
    void refreshUpdatedDemand(FullDemandDetail demand);

    @Event(handlers = AdminDemandsPresenter.class)
    void refreshUpdatedOffer(FullOfferDetail demand);

    @Event(handlers = AdminDemandsPresenter.class)
    void setAllDemands(List<DemandDetail> demands);

    @Event(handlers = AdminDemandInfoPresenter.class)
    void showAdminDemandDetail(FullDemandDetail selectedObject);

    @Event(handlers = AdminOfferInfoPresenter.class)
    void showAdminOfferDetail(FullOfferDetail selectedObject);

    @Event(handlers = AdminDemandsPresenter.class)
    void responseAdminDemandDetail(Widget widget);
    /* <<<<<<<<<<-------- ADMIN DEMANDS -------------------- */
}
