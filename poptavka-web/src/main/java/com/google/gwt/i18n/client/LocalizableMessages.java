package com.google.gwt.i18n.client;

public interface LocalizableMessages extends Messages {

    String logIn();
    String logOut();
    String endDate();
    String emptyField();

    //offers view - buttons
    String answer();
    String refuse();
    String accept();
    //offer table
    String price();
    String demand();
    //demand creation
    String mailAvailable();
    String malformedEmail();
    String shortPassword();
    String strongPassword();
    String semiStrongPassword();
    String passwordsMatch();
    String passwordsUnmatch();

    //status messages, description
    String basicMessage();
    String basicDescription();
    String locMessage();
    String locDescription();
    String catMessage();
    String catDescription();
    String advMessage();
    String advDescription();
    String loginMessage();
    String loginDescription();
    String regMessage();
    String regDescription();

    //global loading popup
    String progressRegisterClient();
    String progressGettingDemandData();
    String progressCreatingDemand();
    String progressLogingUser();
    String wrongLoginDescription();
    String wrongLoginMessage();
}
