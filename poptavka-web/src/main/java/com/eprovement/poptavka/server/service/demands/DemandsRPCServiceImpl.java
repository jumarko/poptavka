/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eprovement.poptavka.server.service.demands;

import com.eprovement.poptavka.client.service.demand.DemandsRPCService;
import com.eprovement.poptavka.server.converter.Converter;
import com.eprovement.poptavka.shared.domain.demand.FullDemandDetail;
import com.eprovement.poptavka.dao.message.MessageFilter;
import com.eprovement.poptavka.domain.common.ResultCriteria;
import com.eprovement.poptavka.domain.demand.Demand;
import com.eprovement.poptavka.domain.message.Message;
import com.eprovement.poptavka.domain.enums.MessageContext;
import com.eprovement.poptavka.domain.enums.MessageUserRoleType;
import com.eprovement.poptavka.domain.message.UserMessage;
import com.eprovement.poptavka.domain.offer.Offer;
import com.eprovement.poptavka.domain.user.BusinessUser;
import com.eprovement.poptavka.domain.user.User;
import com.eprovement.poptavka.exception.MessageException;
import com.eprovement.poptavka.server.service.AutoinjectingRemoteService;
import com.eprovement.poptavka.service.GeneralService;
import com.eprovement.poptavka.service.demand.DemandService;
import com.eprovement.poptavka.service.demand.RatingService;
import com.eprovement.poptavka.service.message.MessageService;
import com.eprovement.poptavka.service.usermessage.UserMessageService;
import com.eprovement.poptavka.shared.domain.adminModule.OfferDetail;
import com.eprovement.poptavka.shared.domain.demand.BaseDemandDetail;
import com.eprovement.poptavka.shared.domain.message.ClientDemandMessageDetail;
import com.eprovement.poptavka.shared.domain.message.MessageDetail;
import com.eprovement.poptavka.shared.domain.message.PotentialDemandMessage;
import com.eprovement.poptavka.shared.exceptions.RPCException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Praso
 */
@Component(DemandsRPCService.URL)
public class DemandsRPCServiceImpl extends AutoinjectingRemoteService implements DemandsRPCService {

    // TODO ivlcek - konstanty nacitat cez lokalizovane rozhranie
    public static final String QUERY_TO_POTENTIAL_DEMAND_SUBJECT = "Dotaz na Vasu zadanu poptavku";
//    public static final String OFFER_TO_POTENTIAL_DEMAND_SUBJECT = "Ponuka na vasu poptavku/nazov dodavatela";
//    public static final String INTERNAL_MESSAGE = "Interna sprava";
    private DemandService demandService;
    private UserMessageService userMessageService;
    private GeneralService generalService;
    private MessageService messageService;
    private RatingService ratingService;
    private Converter<Message, MessageDetail> messageConverter;
    private Converter<Demand, FullDemandDetail> demandConverter;
    private Converter<UserMessage, ClientDemandMessageDetail> clientDemandMessageConverter;
    private Converter<UserMessage, PotentialDemandMessage> potentialDemandMessageConverter;
    private Converter<Demand, BaseDemandDetail> baseDemandConverter;

    public void setRatingService(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Autowired
    public void setGeneralService(GeneralService generalService) {
        this.generalService = generalService;
    }

    @Autowired
    public void setUserMessageService(UserMessageService userMessageService) {
        this.userMessageService = userMessageService;
    }

    @Autowired
    public void setDemandService(DemandService demandService) {
        this.demandService = demandService;
    }

    @Autowired
    public void setMessageConverter(@Qualifier("messageConverter") Converter<Message, MessageDetail> messageConverter) {
        this.messageConverter = messageConverter;
    }

    @Autowired
    public void setDemandConverter(
            @Qualifier("fullDemandConverter") Converter<Demand, FullDemandDetail> demandConverter) {
        this.demandConverter = demandConverter;
    }

    @Autowired
    public void setClientDemandMessageConverter(@Qualifier("clientDemandMessageConverter")
            Converter<UserMessage, ClientDemandMessageDetail> clientDemandMessageConverter) {
        this.clientDemandMessageConverter = clientDemandMessageConverter;
    }

    @Autowired
    public void setPotentialDemandMessageConverter(@Qualifier("potentialDemandMessageConverter")
            Converter<UserMessage, PotentialDemandMessage> potentialDemandMessageConverter) {
        this.potentialDemandMessageConverter = potentialDemandMessageConverter;
    }

    @Autowired
    public void setBaseDemandConverter(
            @Qualifier("baseDemandConverter") Converter<Demand, BaseDemandDetail> baseDemandConverter) {
        this.baseDemandConverter = baseDemandConverter;
    }

    /**
     * Metoda, ktora vrati zoznam klientovych poptavkovych sprav.
     *
     * Pouzitie na UI: odkaz na wireframe - webgres.cz/axure/ -> UC.73 Moje poptavky_new
     *
     * Metoda vrati vsetky spravy #Message, pre ktore plati:
     * - sprava je koren (tj.poptavkova sprava) #message.threadRoot is not null
     * - odosielatel spravy je businessUserId tj. nas klient #message.sender = businessUserId
     * - sprava ma priradenu poptavku #message.demand is not null
     *
     * Pre kazdu poptavkovu spravu z vyselektovaneho zoznamu si musime dotiahnut
     * pocet neprecitanych podsprav. Selekt neprecitanych podspravby mal vyzera nasledovne
     * - message.threadRoot = poptavkova sprava z vyssieho zoznamu
     * - message.messageUserRole.messageContext = MessageContext.QUERY_TO_POTENTIAL_SUPPLIERS_DEMAND
     * (vid metodu sendQueryToPotentialDemand)
     * - UserMessage.message = message.id (konkretna podsprava)
     * - UserMessage.user = businessUserId
     *
     * @param businessUserId - parameter z UI
     * @param clientId - parameter z UI
     * @return ClientDemandMessageDetail
     */
    @Override
    public ArrayList<ClientDemandMessageDetail> getListOfClientDemandMessages(
            long businessUserId, long clientId) throws RPCException {
        ArrayList<ClientDemandMessageDetail> result = new ArrayList();
        BusinessUser businessUser = this.generalService.find(BusinessUser.class, businessUserId);
        Map<Message, Integer> submessageCounts = this.messageService.getListOfClientDemandMessagesAll(businessUser);
        Map<Message, Integer> unreadSubmessageCounts =
                this.messageService.getListOfClientDemandMessagesUnread(businessUser);
        List<UserMessage> userMessages = userMessageService.getUserMessages(
                new ArrayList(submessageCounts.keySet()), businessUser, MessageFilter.EMPTY_FILTER);
        for (UserMessage userMessage : userMessages) {
            ClientDemandMessageDetail detail = clientDemandMessageConverter.convertToTarget(userMessage);
            if (submessageCounts.get(userMessage.getMessage()) == null) {
                detail.setMessageCount(0);
            } else {
                detail.setMessageCount(submessageCounts.get(userMessage.getMessage()).intValue());
            }
            if (unreadSubmessageCounts.get(userMessage.getMessage()) == null) {
                detail.setUnreadSubmessages(0);
            } else {
                detail.setUnreadSubmessages(unreadSubmessageCounts.get(userMessage.getMessage()));
            }
        }
        return result;
    }

    /**
     *
     * TODO - remove this garbage and call {@link UserMessageService#getPotentialDemands)
     *
     * SUPPLIER.
     * Returns messages for PotentialDemandsView's table
     */
    @Override
    public ArrayList<PotentialDemandMessage> getPotentialDemands(long businessUserId) throws RPCException {
        BusinessUser businessUser = this.generalService.find(BusinessUser.class, businessUserId);
        final List<Message> messages = this.messageService.getAllMessages(
                businessUser,
                MessageFilter.MessageFilterBuilder.messageFilter().
                withMessageUserRoleType(MessageUserRoleType.TO).
                withMessageContext(MessageContext.POTENTIAL_SUPPLIERS_DEMAND).
                withResultCriteria(ResultCriteria.EMPTY_CRITERIA).build());
        // TODO ivlcek - prerobit tak aby som nemusel nacitavat list messages, ktoru sluzit len ako
        // parameter pre dalsi dotaz do DB na ziskanie userMessages
        final List<UserMessage> userMessages =
                userMessageService.getUserMessages(messages, businessUser, MessageFilter.EMPTY_FILTER);
        // fill list
        ArrayList<PotentialDemandMessage> potentailDemands = new ArrayList<PotentialDemandMessage>();
        for (UserMessage um : userMessages) {
            PotentialDemandMessage detail = potentialDemandMessageConverter.convertToTarget(um);
            detail.setClientRating(ratingService.getAvgRating(um.getMessage().getDemand().getClient()));
            detail.setMessageCount(messageService.getAllDescendantsCount(um.getMessage(), businessUser));
            detail.setUnreadSubmessages(messageService.getUnreadDescendantsCount(um.getMessage(), businessUser));
            potentailDemands.add(detail);
        }
        return potentailDemands;
    }

    /**
     * COMMON.
     * Change 'star' status of sent messages to chosen value
     */
    @Override
    public void setMessageStarStatus(List<Long> userMessageIds, boolean isStarred) throws RPCException {
        for (Long userMessageId : userMessageIds) {
            UserMessage userMessage = this.generalService.find(UserMessage.class, userMessageId);
            userMessage.setStarred(isStarred);
            this.userMessageService.update(userMessage);
        }
    }

    /**
     * COMMON.
     * Change 'read' status of sent messages to chosen value
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void setMessageReadStatus(List<Long> userMessageIds, boolean isRead) throws RPCException {
        for (Long userMessageId : userMessageIds) {
            UserMessage userMessage = this.generalService.find(UserMessage.class, userMessageId);
            userMessage.setRead(isRead);
            this.userMessageService.update(userMessage);
        }
    }

    @Override
    // TODO call setMessageReadStatus in body
    public ArrayList<MessageDetail> loadSuppliersPotentialDemandConversation(
            long threadId, long userId, long userMessageId) throws RPCException {
        Message threadRoot = messageService.getById(threadId);

        setMessageReadStatus(Arrays.asList(new Long[]{userMessageId}), true);

        User user = this.generalService.find(User.class, userId);
        ArrayList<Message> messages = (ArrayList<Message>) this.messageService.getPotentialDemandConversation(
                threadRoot, user);
        ArrayList<MessageDetail> messageDetailImpls = new ArrayList<MessageDetail>();
        for (Message message : messages) {
            messageDetailImpls.add(messageConverter.convertToTarget(message));
        }
        return messageDetailImpls;
    }

    /**
     * Message sent by supplier about a query to potential demand.
     * @param messageDetailImpl
     * @return message
     */
    @Override
    public MessageDetail sendQueryToPotentialDemand(MessageDetail messageDetailImpl) throws RPCException {
        try {
            Message m = messageService.newReply(this.messageService.getById(
                    messageDetailImpl.getThreadRootId()),
                    this.generalService.find(User.class, messageDetailImpl.getSenderId()));
            m.setBody(messageDetailImpl.getBody());
            m.setSubject(QUERY_TO_POTENTIAL_DEMAND_SUBJECT);
            // TODO set the id correctly, check it
            MessageDetail messageDetailFromDB = messageConverter.convertToTarget(this.messageService.create(m));
            return messageDetailFromDB;
        } catch (MessageException ex) {
            Logger.getLogger(DemandsRPCServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    // TODO Praso - zatial sa tato metoda nepouziva. V ramci refaktotingu RPC metod ju zrejme uplne
    // prekopeme.
    @Override
    public ArrayList<ArrayList<OfferDetail>> getDemandOffers(ArrayList<Long> idList) throws RPCException {
        ArrayList<ArrayList<OfferDetail>> offerList = new ArrayList<ArrayList<OfferDetail>>();
        for (Long id : idList) {
            Demand demand = demandService.getById(id);
            offerList.add(this.toOfferDetailList(demand.getOffers()));
        }
        return offerList;
    }

    // TODO Praso - zatial sa tato metoda nepouziva. V ramci refaktotingu RPC metod ju zrejme uplne
    // prekopeme.
    protected ArrayList<OfferDetail> toOfferDetailList(List<Offer> offerList) {
        ArrayList<OfferDetail> details = new ArrayList<OfferDetail>();
        for (Offer offer : offerList) {
            OfferDetail detail = new OfferDetail();
            detail.setDemandId(offer.getDemand().getId());
            detail.setFinishDate(offer.getFinishDate());
            detail.setPrice(offer.getPrice());
            detail.setSupplierId(offer.getSupplier().getId());
            if (offer.getSupplier().getBusinessUser().getBusinessUserData() != null) {
                detail.setSupplierName(offer.getSupplier().getBusinessUser().getBusinessUserData().getCompanyName());
            } else {
                detail.setSupplierName("unknown");
            }

            details.add(detail);
        }
        return details;
    }

    @Override
    public FullDemandDetail getFullDemandDetail(Long demandId) throws RPCException {
        return demandConverter.convertToTarget(this.demandService.getById(demandId));

    }

    @Override
    public BaseDemandDetail getBaseDemandDetail(Long demandId) throws RPCException {

        return baseDemandConverter.convertToTarget(this.demandService.getById(demandId));

    }
}
