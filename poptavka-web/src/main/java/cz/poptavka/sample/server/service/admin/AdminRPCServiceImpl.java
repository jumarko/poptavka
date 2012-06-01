/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.poptavka.sample.server.service.admin;

import cz.poptavka.sample.domain.activation.ActivationEmail;
import cz.poptavka.sample.shared.domain.converter.AccessRoleConverter;
import cz.poptavka.sample.shared.domain.converter.ActivationEmailConverter;
import cz.poptavka.sample.shared.domain.converter.ClientConverter;
import cz.poptavka.sample.shared.domain.converter.DemandConverter;
import cz.poptavka.sample.shared.domain.converter.InvoiceConverter;
import cz.poptavka.sample.shared.domain.converter.MessageConverter;
import cz.poptavka.sample.shared.domain.converter.OfferConverter;
import cz.poptavka.sample.shared.domain.converter.PaymentConverter;
import cz.poptavka.sample.shared.domain.converter.PaymentMethodConverter;
import cz.poptavka.sample.shared.domain.converter.PermissionConverter;
import cz.poptavka.sample.shared.domain.converter.PreferenceConverter;
import cz.poptavka.sample.shared.domain.converter.ProblemConverter;
import cz.poptavka.sample.shared.domain.converter.SupplierConverter;
import cz.poptavka.sample.shared.domain.adminModule.ActivationEmailDetail;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.Sort;

import cz.poptavka.sample.client.main.common.search.SearchModuleDataHolder;
import cz.poptavka.sample.client.main.common.search.dataHolders.FilterItem;
import cz.poptavka.sample.client.service.demand.AdminRPCService;
import cz.poptavka.sample.domain.address.Locality;
import cz.poptavka.sample.domain.common.OrderType;
import cz.poptavka.sample.domain.demand.Category;
import cz.poptavka.sample.domain.demand.Demand;
import cz.poptavka.sample.domain.invoice.Invoice;
import cz.poptavka.sample.domain.invoice.OurPaymentDetails;
import cz.poptavka.sample.domain.invoice.PaymentMethod;
import cz.poptavka.sample.domain.message.Message;
import cz.poptavka.sample.domain.offer.Offer;
import cz.poptavka.sample.domain.settings.Preference;
import cz.poptavka.sample.domain.user.BusinessUserData;
import cz.poptavka.sample.domain.user.Client;
import cz.poptavka.sample.domain.user.Problem;
import cz.poptavka.sample.domain.user.Supplier;
import cz.poptavka.sample.domain.user.rights.AccessRole;
import cz.poptavka.sample.domain.user.rights.Permission;
import cz.poptavka.sample.exception.MessageException;
import cz.poptavka.sample.server.service.AutoinjectingRemoteService;
import cz.poptavka.sample.service.GeneralService;
import cz.poptavka.sample.service.address.LocalityService;
import cz.poptavka.sample.service.demand.CategoryService;
import cz.poptavka.sample.service.demand.DemandService;
import cz.poptavka.sample.shared.domain.adminModule.AccessRoleDetail;
import cz.poptavka.sample.shared.domain.adminModule.ClientDetail;
import cz.poptavka.sample.shared.domain.adminModule.InvoiceDetail;
import cz.poptavka.sample.shared.domain.adminModule.OfferDetail;
import cz.poptavka.sample.shared.domain.adminModule.PaymentDetail;
import cz.poptavka.sample.shared.domain.adminModule.PaymentMethodDetail;
import cz.poptavka.sample.shared.domain.adminModule.PermissionDetail;
import cz.poptavka.sample.shared.domain.adminModule.PreferenceDetail;
import cz.poptavka.sample.shared.domain.adminModule.ProblemDetail;
import cz.poptavka.sample.shared.domain.demand.FullDemandDetail;
import cz.poptavka.sample.shared.domain.message.MessageDetail;
import cz.poptavka.sample.shared.domain.supplier.FullSupplierDetail;
import cz.poptavka.sample.shared.domain.type.MessageType;
import cz.poptavka.sample.shared.exceptions.RPCException;

/*
 * TODO Martin
 * Vsetky count zrobit inak, ked sa bude riesit tento modul.
 * Vsetky updaty dorobit.
 */
/**
 * @author Martin Slavkovsky
 */
@Component(AdminRPCService.URL)
public class AdminRPCServiceImpl extends AutoinjectingRemoteService implements AdminRPCService {

    private static final long serialVersionUID = 1132667081084321575L;

    private GeneralService generalService;
    private DemandService demandService;
    private LocalityService localityService;
    private CategoryService categoryService;

    @Autowired
    public void setGeneralService(GeneralService generalService) throws RPCException {
        this.generalService = generalService;
    }

    @Autowired
    public void setDemandService(DemandService demandService) throws RPCException {
        this.demandService = demandService;
    }

    @Autowired
    public void setLocalityService(LocalityService localityService) {
        this.localityService = localityService;
    }

    @Autowired
    public void setCategoryService(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**********************************************************************************************
     ***********************  DEMAND SECTION. ************************************************
     **********************************************************************************************/
    @Override
    public Long getAdminDemandsCount(SearchModuleDataHolder searchDataHolder) throws RPCException {
        Search search = null;
        if (searchDataHolder == null) {
            search = new Search(Demand.class);
        } else {
            search = this.setFilters(searchDataHolder, new Search(Demand.class));
        }
        return (long) generalService.count(search);
    }

    @Override
    public List<FullDemandDetail> getAdminDemands(int start, int count,
            SearchModuleDataHolder searchDataHolder, Map<String, OrderType> orderColumns) throws RPCException {
        Search search = new Search(Demand.class);
        if (searchDataHolder != null) {
            search = this.setFilters(searchDataHolder, search);
        }
        search = this.setSortSearch(orderColumns, search);
        search.setFirstResult(start);
        search.setMaxResults(count);
        return new DemandConverter().convertToTargetList(generalService.search(search));
    }

    @Override
    public void updateDemand(FullDemandDetail fullDemandDetail) throws RPCException {
        Demand demand = demandService.getById(fullDemandDetail.getDemandId());
        if (!demand.getType().getDescription().equals(fullDemandDetail.getDemandType())) {
            demand.setType(demandService.getDemandType(fullDemandDetail.getDemandType()));
        }
        //Treba zistovat ci sa kategorie zmenili? Ak ano, ako aby to nebolo narocne?
        List<Category> categories = new ArrayList<Category>();
        for (long catIds : fullDemandDetail.getCategories().keySet()) {
            categories.add(categoryService.getById(catIds));
        }
        demand.setCategories(categories);
        //Treba zistovat ci sa lokality zmenili? Ak ano, ako aby to nebolo narocne?
        List<Locality> localities = new ArrayList<Locality>();
        for (String locCode : fullDemandDetail.getLocalities().keySet()) {
            localities.add(localityService.getLocality(locCode));
        }
        demand.setLocalities(localities);
        demandService.update(demand);
    }

    /**********************************************************************************************
     ***********************  CLIENT SECTION. ************************************************
     **********************************************************************************************/
    @Override
    public Long getAdminClientsCount(SearchModuleDataHolder searchDataHolder) throws RPCException {
        Search search = null;
        if (searchDataHolder == null) {
            search = new Search(Client.class);
        } else {
            search = this.setFilters(searchDataHolder, new Search(Client.class));
        }
        return (long) generalService.count(search);
    }

    @Override
    public List<ClientDetail> getAdminClients(int start, int count,
            SearchModuleDataHolder searchDataHolder, Map<String, OrderType> orderColumns) throws RPCException {
        Search search = new Search(Client.class);
        if (searchDataHolder != null) {
            search = this.setFilters(searchDataHolder, search);
        }
        search = this.setSortSearch(orderColumns, search);
        search.setFirstResult(start);
        search.setMaxResults(count);
        return new ClientConverter().convertToTargetList(generalService.search(search));
    }

    @Override
    public void updateClient(ClientDetail clientDetail) throws RPCException {
        Client client = generalService.find(Client.class, clientDetail.getId());
        generalService.merge(ClientDetail.updateClient(client, clientDetail));
    }

    /**********************************************************************************************
     ***********************  SUPPLIER SECTION. ************************************************
     **********************************************************************************************/
    @Override
    public Long getAdminSuppliersCount(SearchModuleDataHolder searchDataHolder) throws RPCException {
        Search search = null;
        if (searchDataHolder == null) {
            search = new Search(Supplier.class);
        } else {
            search = this.setFilters(searchDataHolder, new Search(Supplier.class));
        }
        return (long) generalService.count(search);
    }

    @Override
    public List<FullSupplierDetail> getAdminSuppliers(int start, int count,
            SearchModuleDataHolder searchDataHolder, Map<String, OrderType> orderColumns) throws RPCException {
        Search search = new Search(Supplier.class);
        if (searchDataHolder != null) {
            search = this.setFilters(searchDataHolder, search);
        }
        search = this.setSortSearch(orderColumns, search);
        search.setFirstResult(start);
        search.setMaxResults(count);
        return new SupplierConverter().convertToTargetList(generalService.search(search));
    }

    @Override
    public void updateSupplier(FullSupplierDetail supplierDetail) throws RPCException {
        Supplier supplier = generalService.find(Supplier.class, supplierDetail.getSupplierId());
        generalService.merge(FullSupplierDetail.updateSupplier(supplier, supplierDetail));
    }

    /**********************************************************************************************
     ***********************  OFFER SECTION. ************************************************
     **********************************************************************************************/
    @Override
    public Long getAdminOffersCount(SearchModuleDataHolder searchDataHolder) throws RPCException {
        Search search = null;
        if (searchDataHolder == null) {
            search = new Search(Offer.class);
        } else {
            search = this.setFilters(searchDataHolder, new Search(Offer.class));
        }
        return (long) generalService.count(search);
    }

    @Override
    public List<OfferDetail> getAdminOffers(int start, int count,
            SearchModuleDataHolder searchDataHolder, Map<String, OrderType> orderColumns) throws RPCException {
        Search search = new Search(Offer.class);
        if (searchDataHolder != null) {
            search = this.setFilters(searchDataHolder, search);
        }
        search = this.setSortSearch(orderColumns, search);
        search.setFirstResult(start);
        search.setMaxResults(count);
        return new OfferConverter().convertToTargetList(generalService.search(search));
    }

    @Override
    public void updateOffer(OfferDetail offerDetail) {
        Offer offer = generalService.find(Offer.class, offerDetail.getId());
        generalService.merge(OfferDetail.updateOffer(offer, offerDetail));
    }

    /**********************************************************************************************
     ***********************  ACCESS ROLE SECTION. ************************************************
     **********************************************************************************************/
    @Override
    public Long getAdminAccessRolesCount(SearchModuleDataHolder searchDataHolder) throws RPCException {
        Search search = null;
        if (searchDataHolder == null) {
            search = new Search(AccessRole.class);
        } else {
            search = this.setFilters(searchDataHolder, new Search(AccessRole.class));
        }
        return (long) generalService.count(search);
    }

    @Override
    public List<AccessRoleDetail> getAdminAccessRoles(int start, int count,
            SearchModuleDataHolder searchDataHolder, Map<String, OrderType> orderColumns) throws RPCException {
        Search search = new Search(AccessRole.class);
        if (searchDataHolder != null) {
            search = this.setFilters(searchDataHolder, search);
        }
        search = this.setSortSearch(orderColumns, search);
        search.setFirstResult(start);
        search.setMaxResults(count);
        return new AccessRoleConverter().convertToTargetList(generalService.search(search));
    }

    @Override
    public void updateAccessRole(AccessRoleDetail accessRoleDetail) throws RPCException {
        AccessRole accessRole = generalService.find(AccessRole.class, accessRoleDetail.getId());
        generalService.merge(AccessRoleDetail.updateAccessRole(accessRole, accessRoleDetail));
    }

    /**********************************************************************************************
     ***********************  EMAIL ACTIVATION SECTION. *******************************************
     **********************************************************************************************/
    @Override
    public Long getAdminEmailsActivationCount(SearchModuleDataHolder searchDataHolder) throws RPCException {
        Search search = null;
        if (searchDataHolder == null) {
            search = new Search(ActivationEmail.class);
        } else {
            search = this.setFilters(searchDataHolder, new Search(ActivationEmail.class));
        }
        return (long) generalService.count(search);
    }

    @Override
    public List<ActivationEmailDetail> getAdminEmailsActivation(int start, int count,
            SearchModuleDataHolder searchDataHolder, Map<String, OrderType> orderColumns) throws RPCException {
        Search search = new Search(ActivationEmail.class);
        if (searchDataHolder != null) {
            search = this.setFilters(searchDataHolder, search);
        }
        search = this.setSortSearch(orderColumns, search);
        search.setFirstResult(start);
        search.setMaxResults(count);
        return new ActivationEmailConverter().convertToTargetList(generalService.search(search));
    }

    @Override
    public void updateEmailActivation(ActivationEmailDetail emailActivationDetail) {
        ActivationEmail emailActivation = generalService.find(ActivationEmail.class, emailActivationDetail.getId());
        generalService.merge(ActivationEmailDetail.updateEmailActivation(emailActivation, emailActivationDetail));
    }

    /**********************************************************************************************
     ***********************  INVOICE SECTION. ****************************************************
     **********************************************************************************************/
    @Override
    public Long getAdminInvoicesCount(SearchModuleDataHolder searchDataHolder) throws RPCException {
        Search search = null;
        if (searchDataHolder == null) {
            search = new Search(Invoice.class);
        } else {
            search = this.setFilters(searchDataHolder, new Search(Invoice.class));
        }
        return (long) generalService.count(search);
    }

    @Override
    public List<InvoiceDetail> getAdminInvoices(int start, int count,
            SearchModuleDataHolder searchDataHolder, Map<String, OrderType> orderColumns) throws RPCException {
        Search search = new Search(Invoice.class);
        if (searchDataHolder != null) {
            search = this.setFilters(searchDataHolder, search);
        }
        search = this.setSortSearch(orderColumns, search);
        search.setFirstResult(start);
        search.setMaxResults(count);
        return new InvoiceConverter().convertToTargetList(generalService.search(search));
    }

    @Override
    public void updateInvoice(InvoiceDetail invoiceDetail) throws RPCException {
        Invoice invoice = generalService.find(Invoice.class, invoiceDetail.getId());
        generalService.merge(InvoiceDetail.updateInvoice(invoice, invoiceDetail));
    }

    /**********************************************************************************************
     ***********************  MESSAGE SECTION. ****************************************************
     **********************************************************************************************/
    @Override
    public Long getAdminMessagesCount(SearchModuleDataHolder searchDataHolder) throws RPCException {
        Search search = null;
        if (searchDataHolder == null) {
            search = new Search(Message.class);
        } else {
            search = this.setFilters(searchDataHolder, new Search(Message.class));
        }
        return (long) generalService.count(search);
    }

    @Override
    public List<MessageDetail> getAdminMessages(int start, int count,
            SearchModuleDataHolder searchDataHolder, Map<String, OrderType> orderColumns) throws RPCException {
        Search search = new Search(Message.class);
        if (searchDataHolder != null) {
            search = this.setFilters(searchDataHolder, search);
        }
        search = this.setSortSearch(orderColumns, search);
        search.setFirstResult(start);
        search.setMaxResults(count);
//        search.setPage(count);
        return new MessageConverter().convertToTargetList(generalService.search(search));
    }

    @Override
    public void updateMessage(MessageDetail messageDetail) throws RPCException {
        Message message = generalService.find(Message.class, messageDetail.getMessageId());
        try {
            generalService.merge(MessageDetail.updateMessage(message, messageDetail));
        } catch (MessageException ex) {
            Logger.getLogger(AdminRPCServiceImpl.class.getName()).log(Level.SEVERE, "Coudn't update message.", ex);
        }
    }

    /**********************************************************************************************
     ***********************  OUR PAYMENT DETAIL SECTION. *****************************************
     **********************************************************************************************/
    @Override
    public Long getAdminOurPaymentDetailsCount(SearchModuleDataHolder searchDataHolder) throws RPCException {
        Search search = null;
        if (searchDataHolder == null) {
            search = new Search(OurPaymentDetails.class);
        } else {
            search = this.setFilters(searchDataHolder, new Search(OurPaymentDetails.class));
        }
        return (long) generalService.count(search);
    }

    @Override
    public List<PaymentDetail> getAdminOurPaymentDetails(int start, int count,
            SearchModuleDataHolder searchDataHolder, Map<String, OrderType> orderColumns) throws RPCException {
        Search search = new Search(OurPaymentDetails.class);
        if (searchDataHolder != null) {
            search = this.setFilters(searchDataHolder, search);
        }
        search = this.setSortSearch(orderColumns, search);
        search.setFirstResult(start);
        search.setMaxResults(count);
        return new PaymentConverter().convertToTargetList(generalService.search(search));
    }

    @Override
    public void updateOurPaymentDetail(PaymentDetail paymentDetail) throws RPCException {
        OurPaymentDetails ourPaymentDetails = generalService.find(OurPaymentDetails.class, paymentDetail.getId());
        generalService.merge(PaymentDetail.updateOurPaymentDetails(ourPaymentDetails, paymentDetail));
    }

    /**********************************************************************************************
     ***********************  PAYMENT METHOD SECTION. ************************************************
     **********************************************************************************************/
    @Override
    public Long getAdminPaymentMethodsCount(SearchModuleDataHolder searchDataHolder) throws RPCException {
        Search search = null;
        if (searchDataHolder == null) {
            search = new Search(PaymentMethod.class);
        } else {
            search = this.setFilters(searchDataHolder, new Search(PaymentMethod.class));
        }
        return (long) generalService.count(search);
    }

    @Override
    public List<PaymentMethodDetail> getAdminPaymentMethods(int start, int count,
            SearchModuleDataHolder searchDataHolder, Map<String, OrderType> orderColumns) throws RPCException {
        Search search = new Search(PaymentMethod.class);
        if (searchDataHolder != null) {
            search = this.setFilters(searchDataHolder, search);
        }
        search = this.setSortSearch(orderColumns, search);
        search.setFirstResult(start);
        search.setMaxResults(count);
        return new PaymentMethodConverter().convertToTargetList(generalService.search(search));
    }

    @Override
    public List<PaymentMethodDetail> getAdminPaymentMethods() {
        final Search search = new Search(PaymentMethod.class);
        search.addSort("id", false);
        return new PaymentMethodConverter().convertToTargetList(generalService.search(search));
    }

    @Override
    public void updatePaymentMethod(PaymentMethodDetail paymentMethodDetail) throws RPCException {
        PaymentMethod paymentMethod = generalService.find(PaymentMethod.class, paymentMethodDetail.getId());
        generalService.merge(PaymentMethodDetail.updatePaymentMethod(paymentMethod, paymentMethodDetail));
    }

    /**********************************************************************************************
     ***********************  PERMISSION SECTION. *************************************************
     **********************************************************************************************/
    @Override
    public Long getAdminPermissionsCount(SearchModuleDataHolder searchDataHolder) throws RPCException {
        Search search = null;
        if (searchDataHolder == null) {
            search = new Search(Permission.class);
        } else {
            search = this.setFilters(searchDataHolder, new Search(Permission.class));
        }
        return (long) generalService.count(search);
    }

    @Override
    public List<PermissionDetail> getAdminPermissions(int start, int count,
            SearchModuleDataHolder searchDataHolder, Map<String, OrderType> orderColumns) throws RPCException {
        Search search = new Search(Permission.class);
        if (searchDataHolder != null) {
            search = this.setFilters(searchDataHolder, search);
        }
        search = this.setSortSearch(orderColumns, search);
        search.setFirstResult(start);
        search.setMaxResults(count);
        return new PermissionConverter().convertToTargetList(generalService.search(search));
    }

    @Override
    public void updatePermission(PermissionDetail permissionDetail) throws RPCException {
        Permission permissionDetails = generalService.find(Permission.class, permissionDetail.getId());
        generalService.merge(PermissionDetail.updatePermission(permissionDetails, permissionDetail));
    }

    /**********************************************************************************************
     ***********************  PREFERENCE SECTION. *************************************************
     **********************************************************************************************/
    @Override
    public Long getAdminPreferencesCount(SearchModuleDataHolder searchDataHolder) throws RPCException {
        Search search = null;
        if (searchDataHolder == null) {
            search = new Search(Preference.class);
        } else {
            search = this.setFilters(searchDataHolder, new Search(Preference.class));
        }
        return (long) generalService.count(search);
    }

    @Override
    public List<PreferenceDetail> getAdminPreferences(int start, int count,
            SearchModuleDataHolder searchDataHolder, Map<String, OrderType> orderColumns) throws RPCException {
        Search search = new Search(Preference.class);
        if (searchDataHolder != null) {
            search = this.setFilters(searchDataHolder, search);
        }
        search = this.setSortSearch(orderColumns, search);
        search.setFirstResult(start);
        search.setMaxResults(count);
        return new PreferenceConverter().convertToTargetList(generalService.search(search));
    }

    @Override
    public void updatePreference(PreferenceDetail preferenceDetail) throws RPCException {
        Preference preference = generalService.find(Preference.class, preferenceDetail.getId());
        generalService.merge(PreferenceDetail.updatePreference(preference, preferenceDetail));
    }

    /**********************************************************************************************
     ***********************  PROBLEM SECTION. ************************************************
     **********************************************************************************************/
    @Override
    public Long getAdminProblemsCount(SearchModuleDataHolder searchDataHolder) throws RPCException {
        Search search = null;
        if (searchDataHolder == null) {
            search = new Search(Problem.class);
        } else {
            search = this.setFilters(searchDataHolder, new Search(Problem.class));
        }
        return (long) generalService.count(search);
    }

    @Override
    public List<ProblemDetail> getAdminProblems(int start, int count,
            SearchModuleDataHolder searchDataHolder, Map<String, OrderType> orderColumns) throws RPCException {
        Search search = new Search(Problem.class);
        if (searchDataHolder != null) {
            search = this.setFilters(searchDataHolder, search);
        }
        search = this.setSortSearch(orderColumns, search);
        search.setFirstResult(start);
        search.setMaxResults(count);
        return new ProblemConverter().convertToTargetList(generalService.search(search));
    }

    @Override
    public void updateProblem(ProblemDetail problemDetail) throws RPCException {
        Problem problem = generalService.find(Problem.class, problemDetail.getId());
        generalService.merge(ProblemDetail.updateProblem(problem, problemDetail));
    }

    /**********************************************************************************************
     ***********************  COMMON METHODS. *************************************************
     **********************************************************************************************/

    private Search setSortSearch(Map<String, OrderType> orderColumns, Search search) {
        List<Sort> sorts = new ArrayList<Sort>();
        for (String str : orderColumns.keySet()) {
            if (orderColumns.get(str) == OrderType.ASC) {
                sorts.add(new Sort(str, false));
            } else {
                sorts.add(new Sort(str, true));
            }
        }
        return search.addSorts(sorts.toArray(new Sort[sorts.size()]));
    }

    private Search filter(Search search, FilterItem item) {
        switch (item.getOperation()) {
            case FilterItem.OPERATION_EQUALS:
                search.addFilterEqual(item.getItem(), item.getValue());
                break;
            case FilterItem.OPERATION_LIKE:
                search.addFilterLike(item.getItem(), "%" + item.getValue().toString() + "%");
                break;
            case FilterItem.OPERATION_IN:
                search.addFilterIn(item.getItem(), item.getValue());
                break;
            case FilterItem.OPERATION_FROM:
                search.addFilterGreaterOrEqual(item.getItem(), item.getValue());
                break;
            case FilterItem.OPERATION_TO:
                search.addFilterLessOrEqual(item.getItem(), item.getValue());
                break;
            default:
                break;
        }
        return search;
    }

    private Search setFilters(SearchModuleDataHolder searchDataHolder, Search search) {
        for (FilterItem item : searchDataHolder.getAttibutes()) {
            if (item.getItem().equals("type")) {
                search.addFilterEqual("type", demandService.getDemandType(item.getValue().toString()));
                //Da sa aj takto? AK ano, mozem odstanit demandService cim zmensim opat download fragment
//                search.addFilterEqual("type", generalService.find(DemandType.class, item.getValue().toString()));
            } else if (item.getItem().equals("companyName")) {
                Collection<BusinessUserData> data = generalService.search(
                        this.filter(new Search(BusinessUserData.class), item));
                search.addFilterIn("businessUser.businessUserData", data);
            } else if (item.getItem().equals("personFirstName")) {
                Collection<BusinessUserData> data = generalService.search(
                        this.filter(new Search(BusinessUserData.class), item));
                search.addFilterIn("businessUser.businessUserData", data);
            } else if (item.getItem().equals("personLastName")) {
                List<BusinessUserData> data = generalService.search(
                        this.filter(new Search(BusinessUserData.class), item));
                search.addFilterIn("businessUser.businessUserData", data);
            } else if (item.getItem().equals("description")) {
                Collection<BusinessUserData> data = generalService.search(
                        this.filter(new Search(BusinessUserData.class), item));
                search.addFilterIn("businessUser.businessUserData", data);
            } else if (item.getItem().equals("state")) {
                //TODO skontrolovat, v message totiz list roli + ci je to rovnaky typ triedy
                search.addFilterIn("roles.MessageUserRole.MessageUserRoleType",
                        MessageType.valueOf(item.getValue().toString()));
            } else {
                this.filter(search, item);
            }
        }
        return search;
    }
}
