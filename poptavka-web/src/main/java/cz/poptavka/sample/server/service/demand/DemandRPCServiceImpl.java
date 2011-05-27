/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.poptavka.sample.server.service.demand;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import cz.poptavka.sample.client.service.demand.DemandRPCService;
import cz.poptavka.sample.domain.address.Locality;
import cz.poptavka.sample.domain.common.ResultCriteria;
import cz.poptavka.sample.domain.demand.Category;
import cz.poptavka.sample.domain.demand.Demand;
import cz.poptavka.sample.domain.demand.DemandStatus;
import cz.poptavka.sample.domain.message.Message;
import cz.poptavka.sample.domain.message.MessageState;
import cz.poptavka.sample.domain.message.MessageUserRole;
import cz.poptavka.sample.domain.message.MessageUserRoleType;
import cz.poptavka.sample.domain.message.UserMessage;
import cz.poptavka.sample.domain.user.Client;
import cz.poptavka.sample.domain.user.Supplier;
import cz.poptavka.sample.server.service.AutoinjectingRemoteService;
import cz.poptavka.sample.service.GeneralService;
import cz.poptavka.sample.service.demand.DemandService;
import cz.poptavka.sample.service.message.MessageService;
import cz.poptavka.sample.service.user.SupplierService;
import cz.poptavka.sample.shared.domain.DemandDetail;
import cz.poptavka.sample.shared.domain.OfferDetail;
import cz.poptavka.sample.service.address.LocalityService;
import cz.poptavka.sample.service.demand.CategoryService;
import cz.poptavka.sample.service.user.ClientService;
import java.util.Date;
import java.util.HashSet;

/**
 * @author Excalibur
 */
public class DemandRPCServiceImpl extends AutoinjectingRemoteService implements DemandRPCService {
    /**
     * generated serialVersonUID.
     */
    private static final long serialVersionUID = -4661806018739964100L;
    private DemandService demandService;
    private ClientService clientService;
    private MessageService messageService;
    private SupplierService supplierService;
    private LocalityService localityService;
    private CategoryService categoryService;
    private GeneralService generalService;

    public DemandService getDemandService() {
        return demandService;
    }

    @Autowired
    public void setDemandService(DemandService demandService) {
        this.demandService = demandService;
    }

    @Autowired
    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
    }

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Autowired
    public void setSupplierService(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @Autowired
    public void setLocalityService(LocalityService localityService) {
        this.localityService = localityService;
    }

    @Autowired
    public void setCategoryService(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Autowired
    public void setGeneralService(GeneralService generalService) {
        this.generalService = generalService;
    }

    @Override
    public DemandDetail createNewDemand(DemandDetail detail, Long cliendId) {
        final Demand demand = new Demand();
        demand.setTitle(detail.getTitle());
        demand.setDescription(detail.getDescription());
        demand.setType(this.demandService.getDemandType(detail.getDemandType()));
        demand.setPrice(detail.getPrice());
        demand.setMaxSuppliers(detail.getMaxOffers());
        demand.setMinRating(detail.getMinRating());
        demand.setStatus(DemandStatus.TEMPORARY);
        demand.setEndDate(detail.getEndDate());
        demand.setValidTo(detail.getExpireDate());
        demand.setClient(this.generalService.find(Client.class, cliendId));

        /** localities **/
        List<Locality> locs = new ArrayList<Locality>();
        for (String loc : detail.getLocalities()) {
            locs.add(getLocalityByExample(loc));
        }
        demand.setLocalities(locs);
        /** categories **/
        List<Category> categories = new ArrayList<Category>();
        for (String cat : detail.getCategories()) {
            categories.add(getCategoryByExample(cat));
        }
        demand.setCategories(categories);

        System.out.println("RPCImpl Client: " + (demand.getClient() == null));
        Demand newDemand = demandService.create(demand);
        // TODO ivlcek - test sending demand to proper suppliers
        sendDemandToSuppliersTest(newDemand);
        return DemandDetail.createDemandDetail(newDemand);
    }

    private Locality getLocalityByExample(String searchString) {
        Locality loc = new Locality();
        loc.setCode(searchString);
        Locality resultLoc = localityService.findByExample(loc).get(0);
        return resultLoc;
    }

    private Category getCategoryByExample(String searchString) {
        Category resultsCat = categoryService.getById(Long.parseLong(searchString));
        return resultsCat;
    }

    /**
     * Method creates a message that is associated with created demand. Message
     * is sent to all suppliers that complies with the demand criteria
     * @param demand
     */
    private void sendDemandToSuppliersTest(Demand demand) {
        // TODO Refaktorovat celu metody s Jurajom
        Set<Supplier> suppliers = new HashSet<Supplier>();
        suppliers.addAll(supplierService.getSuppliers(demand.getCategories()
                .toArray(new Category[demand.getCategories().size()])));
        suppliers.addAll(supplierService.getSuppliers(demand.getLocalities()
                .toArray(new Locality[demand.getLocalities().size()])));

        Message message = new Message();
        message.setBody(demand.getDescription() + " Description might be empty");
        message.setCreated(new Date());
        message.setDemand(demand);
        message.setLastModified(new Date());
        message.setMessageState(MessageState.SENT);
        // TODO ivlcek - chceme aby kazdy dodavatel mal moznost vidiet
        // vsetkych prijemocov spravy s novou poptavkou? Cyklus nizsie to umoznuje
        List<MessageUserRole> messageUserRoles = new ArrayList<MessageUserRole>();
        for (Supplier supplierRole : suppliers) {
            MessageUserRole messageUserRole = new MessageUserRole();
            messageUserRole.setMessage(message);
            messageUserRole.setUser(supplierRole.getBusinessUser());
            messageUserRole.setType(MessageUserRoleType.TO);
            messageUserRoles.add(messageUserRole);
        }
        MessageUserRole messageUserRole = new MessageUserRole();
        messageUserRole.setMessage(message);
        messageUserRole.setType(MessageUserRoleType.SENDER);
        messageUserRole.setUser(demand.getClient().getBusinessUser());
        messageUserRoles.add(messageUserRole);
        message.setRoles(messageUserRoles);
        message.setSender(demand.getClient().getBusinessUser());
        message.setSent(new Date());
        message.setSubject(demand.getTitle());
        message.setThreadRoot(message);
        message = messageService.create(message);
        for (Supplier supplier : suppliers) {
            UserMessage userMessage = new UserMessage();
            userMessage.setIsRead(false);
            userMessage.setIsStarred(false);
            userMessage.setMessage(message);
            userMessage.setUser(supplier.getBusinessUser());
            generalService.save(userMessage);
        }
    }

    /**
     * Method updates demand object in database.
     *
     * @param demandDetail - updated demandDetail from front end
     * @return DemandDetail
     */
    @Override
    public DemandDetail updateDemand(DemandDetail demandDetail) {
        // TODO ivlcek - update entity by sa mal robit jednoduchsie ako toto?
        Demand demand = demandService.getById(demandDetail.getId());
//        demand.setCategories(null);
        demand.setClient(clientService.getById(demandDetail.getClientId()));
//        demand.setDescription(null);
        demand.setEndDate(demandDetail.getEndDate());
//        demand.setExcludedSuppliers(null);
//        demand.setLocalities(null);
        demand.setMaxSuppliers(Integer.valueOf(demandDetail.getMaxOffers()));
        demand.setMinRating(Integer.valueOf(demandDetail.getMinRating()));
//        demand.setOffers(null);
//        demand.setOrigin(null);
        demand.setPrice(demandDetail.getPrice());
//        demand.setStatus(null);
        demand.setTitle(demandDetail.getTitle());
        demand.setType(this.demandService.getDemandType(demandDetail.getDemandType()));
        demand.setValidTo(demandDetail.getExpireDate());
        demandService.update(demand);
        return demandDetail;
    }

    @Override
    public List<DemandDetail> getAllDemands() {
        List<DemandDetail> demandDetails = new ArrayList<DemandDetail>();
        for (Demand demand : demandService.getAll()) {
            demandDetails.add(DemandDetail.createDemandDetail(demand));
        }
        return demandDetails;
    }

    @Override
    public Set<Demand> getDemands(Locality... localities) {
        return demandService.getDemands(localities);
    }

    @Override
    public Set<Demand> getDemands(Category... categories) {
        return demandService.getDemands(categories);
    }

    @Override
    public List<Demand> getDemands(ResultCriteria resultCriteria) {
        return demandService.getAll(resultCriteria);
    }

    @Override
    public Set<Demand> getDemands(ResultCriteria resultCriteria, Locality[] localities) {
        return demandService.getDemands(resultCriteria, localities);
    }

    @Override
    public Set<Demand> getDemands(ResultCriteria resultCriteria, Category[] categories) {
        return demandService.getDemands(resultCriteria, categories);
    }

    public ArrayList<DemandDetail> getClientDemands(long id) {
        Client client = clientService.getById(id);
        return this.toDemandDetailList(client.getDemands());
    }

    @Override
    public ArrayList<ArrayList<OfferDetail>> getDemandOffers(ArrayList<Long> idList) {
        ArrayList<ArrayList<OfferDetail>> offerList = new ArrayList<ArrayList<OfferDetail>>();
        for (Long id : idList) {
            Demand demand = demandService.getById(id);
            offerList.add(this.toOfferDetailList(demand.getOffers()));
        }
        return offerList;
    }
}
