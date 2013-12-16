package com.eprovement.poptavka.client.user.settings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.mvp4g.client.annotation.Presenter;

import com.eprovement.poptavka.client.common.session.Storage;
import com.eprovement.poptavka.client.common.validation.ProvidesValidate;
import com.eprovement.poptavka.client.root.toolbar.ProvidesToolbar;
import com.eprovement.poptavka.client.user.settings.SettingsPresenter.SttingsViewInterface;
import com.eprovement.poptavka.client.user.settings.toolbar.SettingsToolbarView;
import com.eprovement.poptavka.client.user.settings.widget.ClientSettingsPresenter;
import com.eprovement.poptavka.client.user.settings.widget.SecuritySettingsPresenter;
import com.eprovement.poptavka.client.user.settings.widget.SupplierSettingsPresenter;
import com.eprovement.poptavka.client.user.settings.widget.SystemSettingsPresenter;
import com.eprovement.poptavka.client.user.settings.widget.UserSettingsPresenter;
import com.eprovement.poptavka.shared.domain.BusinessUserDetail;
import com.eprovement.poptavka.shared.domain.settings.SettingDetail;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.mvp4g.client.history.NavigationConfirmationInterface;
import com.mvp4g.client.history.NavigationEventCommand;
import com.mvp4g.client.presenter.LazyPresenter;
import com.mvp4g.client.view.LazyView;

@Presenter(view = SettingsView.class)
public class SettingsPresenter
        extends LazyPresenter<SttingsViewInterface, SettingsEventBus>
        implements NavigationConfirmationInterface {

    private UserSettingsPresenter userPresenter;
    private ClientSettingsPresenter clientPresenter;
    private SupplierSettingsPresenter supplierPresenter;
    private SystemSettingsPresenter systemPresenter;
    private SecuritySettingsPresenter securityPresenter;
    //
    private SettingDetail settingsDetail;

    //IsWidget musi byt kvoli funkcii ChildAutoDisplay
    public interface SttingsViewInterface extends LazyView, IsWidget, ProvidesToolbar {

        /** Setters. **/
        void setClientButtonVisibility(boolean visible);

        void settingsUserStyleChange();

        void settingsClientStyleChange();

        void settingsSupplierStyleChange();

        void settingsSystemsStyleChange();

        void settingsSecurityStyleChange();

        /** Getters. **/
        SimplePanel getContentPanel();

        SimplePanel getFooterContainer();

        Widget getWidgetView();

        Button getMenuUserBtn();

        Button getMenuClientBtn();

        Button getMenuSupplierBtn();

        Button getMenuSystemBtn();

        Button getMenuSecurityBtn();
    }

    /**************************************************************************/
    /* General Module events                                                  */
    /**************************************************************************/
    public void onStart() {
        // nothing
    }

    /**
     * Every call of onForward method invokes updateUnreadMessagesCount event that is secured thus user without
     * particular access role can't access it and loginPopupView will be displayed.
     */
    public void onForward() {
        eventBus.setBody(view.getWidgetView());
        eventBus.setFooter(view.getFooterContainer());
        eventBus.setToolbarContent("My Profile", view.getToolbarContent(), true);
        if (!(Storage.getUser() == null && Storage.isAppCalledByURL() != null && Storage.isAppCalledByURL())) {
            eventBus.updateUnreadMessagesCount();
        }
    }

    @Override
    public void confirm(NavigationEventCommand event) {
        String navigationLeaveModule = event.toString().substring(0, event.toString().indexOf("$"));
        //check if contains service selector, in case of SupplierSettings widget where
        //ServiceSelector is presented and it fires navigation event on forward but it is still part of setting module
        if (!navigationLeaveModule.contains("ServiceSelector")) {
            if (Window.confirm(Storage.MSGS.settingsNotificationLeavingPage())) {
                //Leaving without saving - reset validation
                event.fireEvent();
            }
        } else {
            //if same module but different method do nothing
        }
    }

    /**************************************************************************/
    /* Bind                                                                   */
    /**************************************************************************/
    @Override
    public void bindView() {
        ((SettingsToolbarView) view.getToolbarContent()).getUpdateButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (isValid()) {
                    updateProfile();
                } else {
                    ((SettingsToolbarView) view.getToolbarContent()).getUpdateBtnTooltip().show();
                    Timer timer = new Timer() {
                        @Override
                        public void run() {
                            ((SettingsToolbarView) view.getToolbarContent()).getUpdateBtnTooltip().hide();
                        }
                    };
                    timer.schedule(5000);
                }
            }
        });
        view.getMenuUserBtn().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                initUserSettings(view.getContentPanel());
            }
        });
        view.getMenuClientBtn().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                initClientSettings(view.getContentPanel());
            }
        });
        view.getMenuSupplierBtn().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                initSupplierSettings(view.getContentPanel());
            }
        });
        view.getMenuSystemBtn().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                initSystemSettings(view.getContentPanel());
            }
        });
        view.getMenuSecurityBtn().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                initSecuritySettings(view.getContentPanel());
            }
        });
    }

    /**************************************************************************/
    /* Navigation events                                                      */
    /**************************************************************************/
    public void onGoToSettingsModule() {
        eventBus.loadingShow(Storage.MSGS.loading());
        eventBus.setNavigationConfirmation(this);

        view.setClientButtonVisibility(
                Storage.getBusinessUserDetail().getBusinessRoles().contains(
                BusinessUserDetail.BusinessRole.SUPPLIER));

        GWT.log("User ID for settings" + Storage.getUser().getUserId());

        //Need to retrieve business user data as SettingDetail
        //BusinessUser is only subset of settingDetail, and there is no point while
        //login retrieving userSettings because of time it requires.
        eventBus.getLoggedUser(Storage.getUser().getUserId());
    }

    /**************************************************************************/
    /* Business events handled by presenter                                   */
    /**************************************************************************/
    public void onSetSettings(SettingDetail detail) {
        this.settingsDetail = detail;
        //set userSettings widget because it is loaded on startup
        initUserSettings(view.getContentPanel());
        eventBus.loadingHide();
    }

    public void onResponseUpdateSettings(Boolean updated) {
        eventBus.loadingHide();
        if (updated) {
            eventBus.showThankYouPopup(SafeHtmlUtils.fromString(Storage.MSGS.settingsUpdatedOK()), null);
        } else {
            eventBus.showThankYouPopup(SafeHtmlUtils.fromString(Storage.MSGS.settingsUpdatedNotOK()), null);
        }
    }

    /**************************************************************************/
    /* Init Methods                                                           */
    /**************************************************************************/
    public void initUserSettings(SimplePanel holder) {
        if (userPresenter == null) {
            userPresenter = eventBus.addHandler(UserSettingsPresenter.class);
        }
        userPresenter.initUserSettings(holder);
        userPresenter.onSetUserSettings(settingsDetail);
        ((SettingsToolbarView) view.getToolbarContent()).getUpdateButton().setVisible(true);
        view.settingsUserStyleChange();
    }

    public void initClientSettings(SimplePanel holder) {
        if (clientPresenter == null) {
            clientPresenter = eventBus.addHandler(ClientSettingsPresenter.class);
            clientPresenter.onSetClientSettings(settingsDetail);
        }
        clientPresenter.initUserSettings(holder);
        ((SettingsToolbarView) view.getToolbarContent()).getUpdateButton().setVisible(true);
        view.settingsClientStyleChange();
    }

    public void initSupplierSettings(SimplePanel holder) {
        if (supplierPresenter == null) {
            supplierPresenter = eventBus.addHandler(SupplierSettingsPresenter.class);
            supplierPresenter.onSetSupplierSettings(settingsDetail);
        }
        supplierPresenter.initUserSettings(holder);
        ((SettingsToolbarView) view.getToolbarContent()).getUpdateButton().setVisible(true);
        view.settingsSupplierStyleChange();
    }

    public void initSystemSettings(SimplePanel holder) {
        if (systemPresenter == null) {
            systemPresenter = eventBus.addHandler(SystemSettingsPresenter.class);
            systemPresenter.onSetSystemSettings(settingsDetail);
        }
        systemPresenter.initSystemSettings(holder);
        ((SettingsToolbarView) view.getToolbarContent()).getUpdateButton().setVisible(true);
        view.settingsSystemsStyleChange();
    }

    public void initSecuritySettings(SimplePanel holder) {
        if (securityPresenter == null) {
            securityPresenter = eventBus.addHandler(SecuritySettingsPresenter.class);
            securityPresenter.onSetSecuritySettings(settingsDetail);
        }
        securityPresenter.initSecuritySettings(holder);
        ((SettingsToolbarView) view.getToolbarContent()).getUpdateButton().setVisible(false);
        view.settingsSecurityStyleChange();
    }

    /**************************************************************************/
    /* Update settings methods                                                */
    /**************************************************************************/
    private boolean isValid() {
        boolean valid = true;
        if (userPresenter != null) {
            valid = userPresenter.getView().isValid() && valid;
            valid = ((ProvidesValidate) userPresenter.getView().getAddressHolder().getWidget()).isValid() && valid;
        }
        if (clientPresenter != null) {
            valid = clientPresenter.getView().isValid() && valid;
        }
        if (supplierPresenter != null) {
            valid = supplierPresenter.getView().isValid() && valid;
        }
        if (systemPresenter != null) {
            valid = systemPresenter.getView().isValid() && valid;
        }
        return valid;
    }

    private void updateProfile() {
        eventBus.loadingShow(Storage.MSGS.progressUpdatingProfile());

        if (userPresenter != null) {
            userPresenter.updateUserSettings(settingsDetail);
        }
        if (clientPresenter != null) {
            clientPresenter.updateClientSettings(settingsDetail);
        }
        if (supplierPresenter != null) {
            supplierPresenter.updateSupplierSettings(settingsDetail);
        }
        if (systemPresenter != null) {
            systemPresenter.updateSystemSettings(settingsDetail);
        }

        eventBus.requestUpdateSettings(settingsDetail);
    }
}
