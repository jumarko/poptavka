/*
 * Copyright (C), eProvement s.r.o. All rights reserved.
 */
package com.eprovement.poptavka.client.root;

import com.eprovement.poptavka.client.common.ReverseCompositeView;
import com.eprovement.poptavka.client.common.session.CssInjector;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.eprovement.poptavka.client.root.interfaces.IRootView;
import com.eprovement.poptavka.client.root.interfaces.IRootView.IRootPresenter;
import com.eprovement.poptavka.resources.StyleResource;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Represents fundamental page separation to containers: Header, Toolbar, Body.
 * In order to have footer scrollable with body's content, footer is injected in each
 * body's content widget.
 * <b><i>Note:</i></b>
 * Vies should not holds business logic. But they can have independent UI logic.
 * They serves only as presentation layout to provide access for presenter to UI elements.
 *
 * @author Beho, Martin Slavkovsky
 */
public class RootView extends ReverseCompositeView<IRootPresenter> implements
        IRootView {

    private static RootViewUiBinder uiBinder = GWT.create(RootViewUiBinder.class);

    interface RootViewUiBinder extends UiBinder<Widget, RootView> {
    }

    /**************************************************************************/
    /* CSS                                                                    */
    /**************************************************************************/
    /**
     * Inject all recquired styles for this view.
     * <b><i>Note:</i></b>
     * No need to use static definition.
     * This have one advantage and that is more nicer code.
     * Take it as onStart but for Views.
     */
    static {
        StyleResource.INSTANCE.initialStandartStyles().ensureInjected();
        CssInjector.INSTANCE.ensureInitialStylesInjected();
        CssInjector.INSTANCE.ensureLayoutStylesInjected();
    }

    /**************************************************************************/
    /* Attributes                                                             */
    /**************************************************************************/
    /** UiBinder attributes. **/
    @UiField ResizeLayoutPanel body;
    @UiField SimplePanel header, toolbar;

    /**************************************************************************/
    /* Initialization                                                         */
    /**************************************************************************/
    /**
     * Initialize RootView.
     */
    public RootView() {
        initWidget(uiBinder.createAndBindUi(this));
        //ResizeLayoutPanel uses strange styles, that interfere with ours. Therefore remove them.
        body.getElement().removeAttribute("style");
    }

    /**************************************************************************/
    /* Setters                                                                */
    /**************************************************************************/
    /**
     * Sets widget to header container.
     * @param header widget
     */
    @Override
    public void setHeader(IsWidget header) {
        GWT.log("Header widget view set");
        this.header.add(header);

    }

    /**
     * Sets widget to toolbar container.
     * @param toolbar widget
     */
    @Override
    public void setToolbar(IsWidget toolbar) {
        GWT.log("Toolbar widget view set");
        this.toolbar.add(toolbar);

    }

    /**
     * Sets widget to body container.
     * @param body widget
     */
    @Override
    public void setBody(IsWidget body) {
        GWT.log("Body widget view set");
        this.body.clear();
        this.body.add(body);

    }

    /**************************************************************************/
    /* Getters                                                                */
    /**************************************************************************/
    /**
     * Gets body resizable container.
     * @return body's resizable layout container
     */
    @Override
    public ResizeLayoutPanel getBody() {
        return body;
    }
}