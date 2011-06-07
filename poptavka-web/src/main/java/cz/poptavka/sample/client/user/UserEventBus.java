package cz.poptavka.sample.client.user;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.mvp4g.client.annotation.Debug;
import com.mvp4g.client.annotation.Event;
import com.mvp4g.client.annotation.Events;
import com.mvp4g.client.event.EventBusWithLookup;

import cz.poptavka.sample.client.user.admin.tab.AdminDemandInfoPresenter;
import cz.poptavka.sample.client.user.admin.tab.AdministrationPresenter;
import cz.poptavka.sample.client.user.admin.tab.DemandsOperatorPresenter;
import cz.poptavka.sample.client.user.demands.DemandsHistoryConverter;
import cz.poptavka.sample.client.user.demands.DemandsLayoutPresenter;
import cz.poptavka.sample.client.user.demands.tab.MyDemandsPresenter;
import cz.poptavka.sample.client.user.demands.tab.NewDemandPresenter;
import cz.poptavka.sample.client.user.demands.tab.OffersPresenter;
import cz.poptavka.sample.client.user.demands.tab.PotentialDemandsPresenter;
import cz.poptavka.sample.client.user.demands.widgets.DetailWrapperPresenter;
import cz.poptavka.sample.client.user.handler.MessageHandler;
import cz.poptavka.sample.client.user.handler.UserHandler;
import cz.poptavka.sample.client.user.problems.MyProblemsHistoryConverter;
import cz.poptavka.sample.client.user.problems.MyProblemsPresenter;
import cz.poptavka.sample.shared.domain.MessageDetail;
import cz.poptavka.sample.shared.domain.OfferDetail;
import cz.poptavka.sample.shared.domain.UserDetail;
import cz.poptavka.sample.shared.domain.demand.ClientDemandDetail;
import cz.poptavka.sample.shared.domain.demand.OfferDemandDetail;
import cz.poptavka.sample.shared.domain.demand.BaseDemandDetail;
import cz.poptavka.sample.shared.domain.type.ViewType;

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
    @Event(handlers = UserHandler.class)
    void getPotentialDemands(long businessUserId);
    @Event(handlers = PotentialDemandsPresenter.class)
    void responsePotentialDemands(ArrayList<BaseDemandDetail> potentialDemandsList);

    /** Offer demands GETTER/SETTER. **/
    // this same method could be called to MyDemandsPresenter
    // depends on it's demandDetailType and what will be difference in data, probably different
    // method will be used
    @Event(handlers = UserPresenter.class)
    void requestClientOfferDemands();
    @Event(handlers = UserHandler.class)
    void getClientDemandsWithOffers(Long clientId);
    @Event(handlers = OffersPresenter.class)
    void responseClientDemandsWithOffers(ArrayList<OfferDemandDetail> result);

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
    void requestClientId(ClientDemandDetail newDemand);
//
//    @Event(handlers = { MyDemandsPresenter.class, DemandsOperatorPresenter.class }, passive = true)
//    void responseClientDemands(ArrayList<ClientDemandDetail> demands);

    /**
     * common method for displaying Demand Detail in the window.
     * **/
    // TODO implements demandDetail section loading for Wrapper
    @Event(handlers = { UserHandler.class,
            // serves for visual sing, that content is loading
            DetailWrapperPresenter.class })
    void getDemandDetail(Long demandId, ViewType typeOfDetail);

    @Event(handlers = DetailWrapperPresenter.class, passive = true)
    void setDemandDetail(ClientDemandDetail detail, ViewType typeOfDetail);

    /** method for displaying conversation to selected demand. **/
    @Event(handlers = UserPresenter.class)
    void requestPotentialDemandConversation(long messageId, long userMessageId);

    @Event(handlers = MessageHandler.class)
    void getPotentialDemandConversation(long messageId, long userId, long userMessageId);

    @Event(handlers = DetailWrapperPresenter.class, passive = true)
    void setPotentialDemandConversation(ArrayList<MessageDetail> messageList, ViewType wrapperhandlerType);

    /**
     * Bubbling message to send to UserPresenter to get the user ID.
     *
     * @param messageToSend
     *            message to be sent
     */
    @Event(handlers = UserPresenter.class)
    void bubbleMessageSending(MessageDetail messageToSend);

    @Event(handlers = MessageHandler.class)
    void sendQueryToPotentialDemand(MessageDetail messageToSend);

    @Event(handlers = DetailWrapperPresenter.class)
    void addReplyToPotentailDemandConversation(MessageDetail result, ViewType wrapperhandlerType);

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
    void bubbleOfferSending(OfferDetail offerToSend);

    @Event(handlers = MessageHandler.class)
    void sendDemandOffer(OfferDetail offerToSend);

    @Event(handlers = MessageHandler.class)
    void requestPotentialDemandReadStatusChange(ArrayList<Long> messages, boolean isRead);

    /** TODO - ivlcek this could be replaced to new EventBus for Admin. **/
    @Event(handlers = UserHandler.class)
    void getAllDemands();

    @Event(handlers = UserHandler.class)
    void updateDemand(ClientDemandDetail demand);

    @Event(handlers = AdministrationPresenter.class)
    void refreshUpdatedDemand(ClientDemandDetail demand);

    @Event(handlers = AdministrationPresenter.class)
    void setAllDemands(List<ClientDemandDetail> demands);

    @Event(handlers = AdminDemandInfoPresenter.class)
    void showAdminDemandDetail(ClientDemandDetail selectedObject);

    @Event(handlers = AdministrationPresenter.class)
    void responseAdminDemandDetail(Widget widget);

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

    /**
     * Navigation events section. /* Presenters do NOT listen to events when deactivated
     **/
    @Event(handlers = MyDemandsPresenter.class, activate = MyDemandsPresenter.class, deactivate = {
            OffersPresenter.class, NewDemandPresenter.class, PotentialDemandsPresenter.class,
            DemandsOperatorPresenter.class, AdministrationPresenter.class },
            historyConverter = DemandsHistoryConverter.class)
    String invokeMyDemands();

//    @Event(handlers = MyProblemsPresenter.class, activate = MyProblemsPresenter.class, deactivate = {
//            OffersPresenter.class, NewDemandPresenter.class, PotentialDemandsPresenter.class,
//            DemandsOperatorPresenter.class, AdministrationPresenter.class },
//            historyConverter = DemandsHistoryConverter.class)
    @Event(handlers = MyProblemsPresenter.class, historyConverter = MyProblemsHistoryConverter.class)
    String invokeMyProblems();

    @Event(handlers = OffersPresenter.class, activate = OffersPresenter.class, deactivate = { MyDemandsPresenter.class,
            NewDemandPresenter.class, PotentialDemandsPresenter.class, DemandsOperatorPresenter.class,
            AdministrationPresenter.class }, historyConverter = DemandsHistoryConverter.class)
    String invokeOffers();

    @Event(handlers = NewDemandPresenter.class, activate = NewDemandPresenter.class, deactivate = {
            OffersPresenter.class, MyDemandsPresenter.class, PotentialDemandsPresenter.class,
            DemandsOperatorPresenter.class, AdministrationPresenter.class },
            historyConverter = DemandsHistoryConverter.class)
    String invokeNewDemand();

    @Event(handlers = PotentialDemandsPresenter.class, activate = PotentialDemandsPresenter.class, deactivate = {
            OffersPresenter.class, MyDemandsPresenter.class, NewDemandPresenter.class, DemandsOperatorPresenter.class,
            AdministrationPresenter.class }, historyConverter = DemandsHistoryConverter.class)
    String invokePotentialDemands();

    @Event(handlers = DemandsOperatorPresenter.class, activate = DemandsOperatorPresenter.class, deactivate = {
            OffersPresenter.class, MyDemandsPresenter.class, PotentialDemandsPresenter.class, NewDemandPresenter.class,
            AdministrationPresenter.class }, historyConverter = DemandsHistoryConverter.class)
    String invokeDemandsOperator();

    @Event(handlers = AdministrationPresenter.class, activate = AdministrationPresenter.class, deactivate = {
            OffersPresenter.class, MyDemandsPresenter.class, PotentialDemandsPresenter.class, NewDemandPresenter.class,
            DemandsOperatorPresenter.class }, historyConverter = UserHistoryConverter.class)
    String invokeAdministration();

    /** Navigation Events section END **/

    /**
     * hacky later fire event Needed when refreshing in User Section - refresh not neededi in prod.
     **/
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
    void createDemand(ClientDemandDetail demand, Long id);

    @Event(forwardToParent = true)
    void setUserLayout();

    @Event(forwardToParent = true)
    void loadingHide();

    @Event(handlers = UserPresenter.class)
    void setUserInteface(StyleInterface widgetView);

    @Event(handlers = UserPresenter.class)
    void clearUserOnUnload();

    /** TEST CLIENT DEMANDS ON MESSAGE BASED SYSTEM **/
    @Event(handlers = MessageHandler.class)
    void getClientDemands(Long userId, int fakeParameter);

    @Event(handlers = MyDemandsPresenter.class)
    void responseClientDemands(ArrayList<MessageDetail> result);

}
