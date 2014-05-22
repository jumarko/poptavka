/*
 * Copyright (C) 2011, eProvement s.r.o. All rights reserved.
 */
package com.eprovement.poptavka.server.service.admin;

import com.eprovement.poptavka.client.service.admin.AdminRPCService;
import com.eprovement.poptavka.domain.address.Locality;
import com.eprovement.poptavka.domain.common.Origin;
import com.eprovement.poptavka.domain.demand.Demand;
import com.eprovement.poptavka.domain.enums.CommonAccessRoles;
import com.eprovement.poptavka.domain.enums.DemandStatus;
import com.eprovement.poptavka.domain.message.Message;
import com.eprovement.poptavka.domain.message.UserMessage;
import com.eprovement.poptavka.domain.user.BusinessUser;
import com.eprovement.poptavka.domain.user.Client;
import com.eprovement.poptavka.domain.user.User;
import com.eprovement.poptavka.server.converter.Converter;
import com.eprovement.poptavka.server.converter.SearchConverter;
import com.eprovement.poptavka.server.security.PoptavkaUserAuthentication;
import com.eprovement.poptavka.server.service.AutoinjectingRemoteService;
import com.eprovement.poptavka.service.GeneralService;
import com.eprovement.poptavka.service.demand.DemandService;
import com.eprovement.poptavka.service.demand.PotentialDemandService;
import com.eprovement.poptavka.service.message.MessageService;
import com.eprovement.poptavka.service.user.ClientService;
import com.eprovement.poptavka.service.usermessage.UserMessageService;
import com.eprovement.poptavka.shared.selectors.catLocSelector.ICatLocDetail;
import com.eprovement.poptavka.shared.domain.adminModule.AdminDemandDetail;
import com.eprovement.poptavka.shared.domain.adminModule.AdminClientDetail;
import com.eprovement.poptavka.shared.domain.demand.OriginDetail;
import com.eprovement.poptavka.shared.domain.message.MessageDetail;
import com.eprovement.poptavka.shared.domain.message.UnreadMessagesDetail;
import com.eprovement.poptavka.shared.exceptions.ApplicationSecurityException;
import com.eprovement.poptavka.shared.exceptions.RPCException;
import com.eprovement.poptavka.shared.search.SearchDefinition;
import com.googlecode.genericdao.search.Field;
import com.googlecode.genericdao.search.Search;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * This RPC handles all requests for Admin module.
 * TODO LATER Martin implement all features if needed.
 * @author Martin Slavkovsky
 */
@Configurable
public class AdminRPCServiceImpl extends AutoinjectingRemoteService implements AdminRPCService {

    /**************************************************************************/
    /* Attributes                                                             */
    /**************************************************************************/
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminRPCServiceImpl.class);
    /** Services. **/
    private DemandService demandService;
    private PotentialDemandService potentialDemandService;
    @Autowired
    private GeneralService generalService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserMessageService userMessageService;

    @Autowired //Set method for seting mock objects in tests
    public void setDemandService(DemandService demandService) {
        this.demandService = demandService;
    }

    @Autowired //Set method for seting mock objects in tests
    public void setPotentialDemandService(PotentialDemandService potentialDemandService) {
        this.potentialDemandService = potentialDemandService;
    }

    /** Converters. **/
    @Autowired
    private Converter<Locality, ICatLocDetail> localityConverter;
    @Autowired
    private Converter<Demand, AdminDemandDetail> adminDemandConverter;
    @Autowired
    private Converter<UserMessage, MessageDetail> userMessageConverter;
    @Autowired
    private Converter<Client, AdminClientDetail> adminClientConverter;
    @Autowired
    private Converter<Origin, OriginDetail> originConverter;
    @Autowired
    private SearchConverter searchConverter;

    /**************************************************************************/
    /*  NEW DEMANDS SECTION.                                                  */
    /**************************************************************************/
    @Override
    @Secured(CommonAccessRoles.ADMIN_ACCESS_ROLE_CODE)
    public Integer getAdminNewDemandsCount() throws RPCException, ApplicationSecurityException {
        return userMessageService.getAdminNewDemandsCount().intValue();
    }

    @Override
    @Secured(CommonAccessRoles.ADMIN_ACCESS_ROLE_CODE)
    public List<AdminDemandDetail> getAdminNewDemands(SearchDefinition searchDefinition)
        throws RPCException, ApplicationSecurityException {

        final List<Demand> demands = userMessageService.getAdminNewDemands(
            searchDefinition.getFirstResult(), searchDefinition.getMaxResult());

        return adminDemandConverter.convertToTargetList(demands);
    }

    /**************************************************************************/
    /*  ASSIGNED DEMANDS SECTION.                                             */
    /**************************************************************************/
    @Override
    @Secured(CommonAccessRoles.ADMIN_ACCESS_ROLE_CODE)
    public Integer getAdminAssignedDemandsCount(
        long userId, SearchDefinition searchDefinition, DemandStatus demandStatus)
        throws RPCException, ApplicationSecurityException {
        return Long.valueOf(
            userMessageService.getAdminConversationsWithDemandStatusCount(userId, demandStatus)).intValue();
    }

    @Override
    @Secured(CommonAccessRoles.ADMIN_ACCESS_ROLE_CODE)
    public List<AdminDemandDetail> getAdminAssignedDemands(
        long userId, DemandStatus demandStatus, SearchDefinition searchDefinition)
        throws RPCException, ApplicationSecurityException {
        final Search search = searchConverter.convertToSource(UserMessage.class, searchDefinition);
        final Map<UserMessage, Integer> latestUserMessagesWithCount
            = userMessageService.getAdminConversationsWithDemandStatus(userId, demandStatus, search);

        ArrayList<AdminDemandDetail> adminDemandDetails = new ArrayList<AdminDemandDetail>();

        for (UserMessage um : latestUserMessagesWithCount.keySet()) {
            AdminDemandDetail detail = new AdminDemandDetail();
            // Client part
            detail.setUserId(um.getMessage().getDemand().getClient().getBusinessUser().getId());
            detail.setClientId(um.getMessage().getDemand().getClient().getId());
            //why not: um.getMessage().getSender().getId() ???
            detail.setSenderId(um.getMessage().getThreadRoot().getSender().getId());
            // Supplier part
//            detail.setSupplierId(supplier);
            // Message part
            detail.setThreadRootId(um.getMessage().getThreadRoot().getId());
            // UserMessage part
            detail.setUserMessageId(um.getId());
            detail.setStarred(um.isStarred());
            detail.setMessagesCount(latestUserMessagesWithCount.get(um));
            detail.setRead(um.isRead());
            // Demand part
            detail.setDemandId(um.getMessage().getDemand().getId());
            detail.setDemandTitle(um.getMessage().getDemand().getTitle());
            detail.setLocalities(localityConverter.convertToTargetList(um.getMessage().getDemand().getLocalities()));
            detail.setCreated(um.getMessage().getDemand().getCreatedDate());
            detail.setValidTo(um.getMessage().getDemand().getValidTo());

            adminDemandDetails.add(detail);
        }
        return adminDemandDetails;
    }

    /**************************************************************************/
    /*  ACTIVE DEMANDS SECTION.                                               */
    /**************************************************************************/
    @Override
    @Secured(CommonAccessRoles.ADMIN_ACCESS_ROLE_CODE)
    public Integer getAdminActiveDemandsCount(SearchDefinition searchDefinition) throws
        RPCException, ApplicationSecurityException {
        Search search = searchConverter.convertToSource(Demand.class, searchDefinition);
        search.addFilterIn("status", Arrays.asList(
            DemandStatus.ACTIVE, DemandStatus.OFFERED, DemandStatus.ASSIGNED, DemandStatus.PENDINGCOMPLETION));
        return generalService.count(search);
    }

    @Override
    @Secured(CommonAccessRoles.ADMIN_ACCESS_ROLE_CODE)
    public List<AdminDemandDetail> getAdminActiveDemands(SearchDefinition searchDefinition)
        throws RPCException, ApplicationSecurityException {

        Search search = searchConverter.convertToSource(Demand.class, searchDefinition);
        search.addFilterIn("status", Arrays.asList(
            DemandStatus.ACTIVE, DemandStatus.OFFERED, DemandStatus.ASSIGNED, DemandStatus.PENDINGCOMPLETION));

        final List<Demand> demands = generalService.search(search);
        return adminDemandConverter.convertToTargetList(demands);
    }

    @Override
    @Secured(CommonAccessRoles.ADMIN_ACCESS_ROLE_CODE)
    public void approveDemands(Set<AdminDemandDetail> demandsToApprove) throws
        RPCException, ApplicationSecurityException {
        LOGGER.info("action=approve_demands status=start");
        for (AdminDemandDetail demandDetail : demandsToApprove) {
            try {
                final Demand demand = demandService.getById(demandDetail.getDemandId());
                demandService.activateDemand(demand);
                potentialDemandService.sendDemandToPotentialSuppliers(demand);
            } catch (Exception e) {
                LOGGER.warn("action=approve_demands status=error demand_id={}", demandDetail.getDemandId(), e);
            }

        }
        LOGGER.info("action=approve_demands status=start");
    }

    /**
     * This method will update number of unread messages of logged user.
     * Since this RPC class requires access of authenticated user (see security-web.xml) this method will be called
     * only when PoptavkaUserAuthentication object exist in SecurityContextHolder and we can retrieve userId.
     *
     * @return UnreadMessagesDetail with number of unread messages and other info to be displayed after users logs in
     * @throws RPCException
     * @throws ApplicationSecurityException
     */
    @Override
    @Secured(CommonAccessRoles.ADMIN_ACCESS_ROLE_CODE)
    public UnreadMessagesDetail updateUnreadMessagesCount() throws RPCException, ApplicationSecurityException {
        Long userId = ((PoptavkaUserAuthentication) SecurityContextHolder.getContext().getAuthentication()).getUserId();
        Search unreadMessagesSearch = new Search(UserMessage.class);
        unreadMessagesSearch.addFilterNotNull("message.demand");
        unreadMessagesSearch.addFilterEqual("isRead", false);
        unreadMessagesSearch.addFilterEqual("user.id", userId.longValue());
        unreadMessagesSearch.addField("id", Field.OP_COUNT);
        unreadMessagesSearch.setResultMode(Search.RESULT_SINGLE);
        UnreadMessagesDetail unreadMessagesDetail = new UnreadMessagesDetail();
        unreadMessagesDetail.setUnreadMessagesCount(
            ((Long) generalService.searchUnique(unreadMessagesSearch)).intValue());
        Search unreadSystemMessagesSearch = new Search(UserMessage.class);
        unreadSystemMessagesSearch.addFilterNull("message.demand");
        unreadSystemMessagesSearch.addFilterEqual("isRead", false);
        unreadSystemMessagesSearch.addFilterEqual("user.id", userId.longValue());
        unreadSystemMessagesSearch.addField("id", Field.OP_COUNT);
        unreadSystemMessagesSearch.setResultMode(Search.RESULT_SINGLE);
        unreadMessagesDetail.setUnreadSystemMessageCount(
            ((Long) generalService.searchUnique(unreadSystemMessagesSearch)).intValue());
        return unreadMessagesDetail;
    }

    /**
     * Gets all inbox and sent user messages UsereMessage of given user and thread root.
     * Once loaded, all user messages are set as read, see isRead attribute of UserMessage.
     *
     * @param threadRootId is root message id
     * @param loggedUserId can be either supplier's or client's user Id
     * @return list of all messages in this thread
     * @throws RPCException
     */
    @Override
    @Secured(CommonAccessRoles.CLIENT_ACCESS_ROLE_CODE)
    public List<MessageDetail> getConversation(long threadRootId, long loggedUserId, long counterPartyUserId)
        throws RPCException, ApplicationSecurityException {
        final List<UserMessage> userMessages = getConversationUserMessages(
            threadRootId, loggedUserId, counterPartyUserId);
        return userMessageConverter.convertToTargetList(userMessages);
    }

    private List<UserMessage> getConversationUserMessages(long threadRootId, long loggedUserId,
        long counterPartyUserId) {
        Message threadRoot = messageService.getById(threadRootId);

        User user = this.generalService.find(User.class, loggedUserId);
        User counterparty = this.generalService.find(User.class, counterPartyUserId);
        return userMessageService.getConversation(user, counterparty, threadRoot);
    }

    /**
     * Creates conversation between <code>Client</code> and Admin/Operator user. Conversation is created in such a
     * way that new <code>UserMessage</code> is created for every <code>User</code> who invokes this method. Thus
     * enabling the user to write a reply message to <code>Client</code> in order to update <code>Demand</code>
     * description or title before this demand is approved.
     *
     * @param demandId for which the conversation is created
     * @param userAdminId id of operator or admin user
     * @return thread root id
     * @throws RPCException
     * @throws ApplicationSecurityException
     */
    @Override
    @Secured(CommonAccessRoles.ADMIN_ACCESS_ROLE_CODE)
    public Long createConversation(long demandId, long userAdminId) throws RPCException,
        ApplicationSecurityException {
        Demand demand = demandService.getById(demandId);
        Message threadRootMessage = messageService.getThreadRootMessage(demand);
        userMessageService.getAdminUserMessage(threadRootMessage, generalService.find(User.class, userAdminId));
        return threadRootMessage.getId();
    }

    /**************************************************************************/
    /*  Admin Clients                                                         */
    /**************************************************************************/
    /**
     * {@inheritDoc}
     */
    @Override
    @Secured(CommonAccessRoles.ADMIN_ACCESS_ROLE_CODE)
    public Integer getClientsCount(SearchDefinition searchDefinition)
        throws RPCException, ApplicationSecurityException {

        return generalService.count(searchConverter.convertToSource(Client.class, searchDefinition));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Secured(CommonAccessRoles.ADMIN_ACCESS_ROLE_CODE)
    public List<AdminClientDetail> getClients(SearchDefinition searchDefinition)
        throws RPCException, ApplicationSecurityException {

        Search search = searchConverter.convertToSource(Client.class, searchDefinition);
        return adminClientConverter.convertToTargetList(generalService.search(search));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Secured(CommonAccessRoles.ADMIN_ACCESS_ROLE_CODE)
    public void changeEmail(long userId, String newEmail) throws RPCException, ApplicationSecurityException {
        User user = generalService.find(User.class, userId);
        user.setEmail(newEmail);
        generalService.merge(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Secured(CommonAccessRoles.ADMIN_ACCESS_ROLE_CODE)
    public void setUserOrigin(long userId, long originId) throws RPCException, ApplicationSecurityException {
        BusinessUser user = generalService.find(BusinessUser.class, userId);
        if (originId == 0) { //no origin
            user.setOrigin(null);
        } else {
            user.setOrigin(generalService.find(Origin.class, originId));
        }
        generalService.merge(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Secured(CommonAccessRoles.ADMIN_ACCESS_ROLE_CODE)
    public List<OriginDetail> getOrigins() throws RPCException, ApplicationSecurityException {
        return originConverter.convertToTargetList(generalService.findAll(Origin.class));
    }
}
