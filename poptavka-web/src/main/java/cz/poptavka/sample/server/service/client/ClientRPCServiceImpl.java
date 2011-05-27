package cz.poptavka.sample.server.service.client;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.googlecode.genericdao.search.Search;

import cz.poptavka.sample.client.service.demand.ClientRPCService;
import cz.poptavka.sample.domain.address.Address;
import cz.poptavka.sample.domain.address.Locality;
import cz.poptavka.sample.domain.common.Status;
import cz.poptavka.sample.domain.product.Service;
import cz.poptavka.sample.domain.product.UserService;
import cz.poptavka.sample.domain.settings.Notification;
import cz.poptavka.sample.domain.settings.NotificationItem;
import cz.poptavka.sample.domain.settings.Period;
import cz.poptavka.sample.domain.user.BusinessUser;
import cz.poptavka.sample.domain.user.BusinessUserData;
import cz.poptavka.sample.domain.user.Client;
import cz.poptavka.sample.domain.user.Verification;
import cz.poptavka.sample.server.service.AutoinjectingRemoteService;
import cz.poptavka.sample.service.GeneralService;
import cz.poptavka.sample.service.user.ClientService;
import cz.poptavka.sample.service.user.UserSearchCriteria;
import cz.poptavka.sample.shared.domain.UserDetail;

public class ClientRPCServiceImpl extends AutoinjectingRemoteService implements ClientRPCService {

    /**
     * Generated serialVersionUID.
     */
    private static final long serialVersionUID = -5905531608577218017L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientRPCServiceImpl.class);
    private GeneralService generalService;
    private ClientService clientService;

    public ArrayList<UserDetail> getAllClients() {
        // TODO do we need this method?
        return null;
    }

    @Autowired
    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
    }

    @Autowired
    public void setGeneralService(GeneralService generalService) {
        this.generalService = generalService;
    }

    /**
     * Create new Client - person or company.
     *
     * TODO: website assignation in backend
     */
    public UserDetail createNewClient(UserDetail clientDetail) {
        Client newClient = new Client();
        /** Person is mandatory for person client and for company client as well. **/
        final BusinessUserData businessUserData = new BusinessUserData.Builder().
                companyName(clientDetail.
                getCompanyName()).
                personFirstName(clientDetail.getFirstName()).
                personLastName(clientDetail.getLastName()).
                phone(clientDetail.getPhone()).
                identificationNumber(clientDetail.
                getIdentifiacationNumber()).
                taxId(clientDetail.getTaxId()).build();
        newClient.getBusinessUser().setBusinessUserData(businessUserData);
        /** Address. **/
        Locality city = this.getLocality(clientDetail.getAddress().getCityName());
        Address address = new Address();
        address.setCity(city);
        address.setStreet(clientDetail.getAddress().getStreet());
        address.setZipCode(clientDetail.getAddress().getZipCode());
        List<Address> addresses = new ArrayList<Address>();
        addresses.add(address);
        newClient.getBusinessUser().setAddresses(addresses);
        /** Login & pwd information. **/
        newClient.getBusinessUser().setEmail(clientDetail.getEmail());
        newClient.getBusinessUser().setPassword(clientDetail.getPassword());
        /** Set service for new client **/
        UserService userService = new UserService();
        userService.setUser(newClient.getBusinessUser());
        userService.setStatus(Status.INACTIVE);
        // TODO ivlcek - nastavit datum vytvorenia UserService aby sme mohli
        // objednannu service zrusit ak client neaktivuje svoj ucet do 14 dni
        // budeme to ziskavat cez AUD entitu alebo novy atribut. AUD entitu pre
        // UserService nemame a je urcite nutna
        // TODO ivlcek - create constants for ciselnik Services or use Constants class
        userService.setService(this.generalService.find(Service.class, 4L));
        List<UserService> userServices = new ArrayList<UserService>();
        userServices.add(userService);
        newClient.getBusinessUser().setUserServices(userServices);
        /** Notifications for new client. **/
        List<NotificationItem> notificationItems = new ArrayList<NotificationItem>();
        NotificationItem notificationItem = new NotificationItem();
        // TODO ivlcek - create constant for Notifications in DB
        notificationItem.setNotification(this.generalService.find(Notification.class, 10L));
        notificationItem.setEnabled(true);
        notificationItem.setPeriod(Period.INSTANTLY);
        notificationItems.add(notificationItem);
        newClient.getBusinessUser().getSettings().setNotificationItems(notificationItems);
        newClient.getBusinessUser().getBusinessUserRoles().add(newClient);
        newClient.setVerification(Verification.UNVERIFIED);
//        newClient.getBusinessUser().getBusinessUserRoles().add(newClient);
        System.out.println("Verification " + newClient.getVerification().toString());
        /** TODO ivlcek - email activation. **/

        newClient = clientService.create(newClient);
        return this.toUserDetail(newClient.getBusinessUser().getBusinessUserRoles());
    }

    @Override
    public UserDetail verifyClient(UserDetail clientDetail) {
        final List<Client> peristedClient = this.clientService.searchByCriteria(
                UserSearchCriteria.Builder.userSearchCriteria()
                .withEmail(clientDetail.getEmail())
                .withPassword(clientDetail.getPassword())
                .build());
        if (peristedClient.isEmpty()) {
            return new UserDetail();
        } else {
            Client user = peristedClient.get(0);
            return toUserDetail(user.getBusinessUser().getBusinessUserRoles());
        }
    }

    @Override
    public boolean checkFreeEmail(String email) {
        // try to find user with given email
        final BusinessUser userByEmail = (BusinessUser) generalService.searchUnique(
                new Search(BusinessUser.class).addFilterEqual("email", email));

        // email is free if no such user exists
        return userByEmail == null;
    }

}
