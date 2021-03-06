/*
 * Copyright (C), eProvement s.r.o. All rights reserved.
 */
package com.eprovement.poptavka.client.root.toolbar;

import com.eprovement.poptavka.client.root.RootEventBus;
import com.eprovement.poptavka.client.root.interfaces.IToolbar;
import com.eprovement.poptavka.resources.StyleResource;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import com.mvp4g.client.annotation.Presenter;
import com.mvp4g.client.presenter.LazyPresenter;

/**
 * Toolbar consists of four parts: left icon, title, custom content, right icon.
 * Left and Right icon serves for providing animation functionality for <b>3-layout-responsive-views</b>.
 *
 * <br/>
 * View is <b>3-layout-responsive-view</b> if its uiBinders constucts following hierarchy:
 * <pre>
 * {@code
 * <g:SimplePanel styleName='{res.layout.leftContainer}' debugId='leftContainer'/>
 * <b:FluidContainer styleName='{res.layout.contentContainer} user'>
 *      <b:FluidRow>
 *          <b:Column size="7"  addStyleNames='{res.initial.expandOnSmall}'/>
 *          <b:Column size="5"  addStyleNames='span-detail'>
 *              <g:SimplePanel styleName='{res.layout.detailContainer}' debugId='detailPanel'/>
 *          </b:Column>
 *      </b:FluidRow>
 * </b:FluidContainer>
 * }
 * </pre>
 * Left and Right panels can be implemented separately or you can miss one or both.
 * <b><i>Note:</i></b>
 * Toolbar consists of two parts: general toolbar - represented by this widget and
 * custom toolbar - provided by each module.
 * Animation is enabled by responsive design. Left sliding panel animates for tiny-middle screens.
 * Right sliding panel animates for tiny-small screen.s
 *
 * @author Martin Slavkovsky
 * @since 10.7.2013
 */
@Presenter(view = ToolbarView.class)
public class ToolbarPresenter extends LazyPresenter<IToolbar.View, RootEventBus>
    implements IToolbar.Presenter {

    /**************************************************************************/
    /* General Module events                                                  */
    /**************************************************************************/
    /**
     * Sets toolbar widget to page layout.
     */
    public void onStart() {
        eventBus.setToolbar(view);
    }

    /**************************************************************************/
    /* Attributes                                                             */
    /**************************************************************************/
    private ResponsiveLayoutSelectors animation = GWT.create(ResponsiveLayoutSelectors.class);
    private boolean isCategoryPanelOpen = false;
    private boolean isDetailPanelOpen = false;
    /** Constants. **/
    private static final String SLIDE_PX_CATEGORY = "330px";
    private static final String SLIDE_PX_DETAIL = "330px";

    /**************************************************************************/
    /* Bind handlers                                                          */
    /**************************************************************************/
    /**
     * Bind handlers for Left & Right sliding menu icon.
     */
    @Override
    public void bindView() {
        if (view.getLeftSlidingMenuIcon() != null) {
            view.getLeftSlidingMenuIcon().addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    openCategoryTreePanel(!isCategoryPanelOpen);
                }
            });
        }
        if (view.getRightSlidingMenuIcon() != null) {
            view.getRightSlidingMenuIcon().addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    openDetailPanel(!isDetailPanelOpen);
                }
            });
        }
    }

    /**************************************************************************/
    /* Business events                                                        */
    /**************************************************************************/
    /**
     * Sets custom toolbar widget.
     * @param title of toolbar
     * @param content - custom toolbar widget
     */
    public void onSetToolbarContent(final String title, final Widget content) {
        //Must be in scheduler in case refresh is used
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

            @Override
            public void execute() {
                view.setToolbarContent(title, content);
            }
        });
    }

    /**
     * Sets left and right icons visibility.
     */
    public void onToolbarRefresh() {
        view.refresh();
    }

    /**
     * Removes animation styles on resize.
     * When animation is used it overrides application styles with its own.
     * It is not a problem unless application uses responsive design.
     * When resizing, responsive design should take care to redesign application,
     * but it is not if animation took place and overrided them.
     * Therefore remove them on resize.
     */
    public void onResize(int actualWidth) {
        if (actualWidth < 480) {
            //nothing by default
        } else if (480 <= actualWidth && actualWidth < 767) {
            view.getRightSlidingMenuIcon().setResource(StyleResource.INSTANCE.images().toolbarButtonRightToOpen());
        } else if (767 <= actualWidth && actualWidth < 1200) {
            view.getLeftSlidingMenuIcon().setResource(StyleResource.INSTANCE.images().toolbarButtonLeftToOpen());
            animation.getRightSlidingPanel().removeAttr("style");
            isDetailPanelOpen = false;
        } else if (1200 <= actualWidth) {
            animation.getLeftSlidingPanel().removeAttr("style");
            isCategoryPanelOpen = false;
        }
    }

    /**
     * Opens right sliding panel.
     */
    public void onOpenDetail() {
        if (Document.get().getClientWidth() <= 767 && !isDetailPanelOpen) {
            openDetailPanel(true);
        }
    }

    /**
     * Close left sliding panel.
     */
    public void onCloseSubMenu() {
        if (Document.get().getClientWidth() <= 767 && isCategoryPanelOpen) {
            openCategoryTreePanel(false);
        }
    }

    /**************************************************************************/
    /* Helper methods                                                         */
    /**************************************************************************/
    /**
     * Animates left sliding panel.
     * @param open - true to slide left sliding panel to be visible, false otherwise
     */
    private void openCategoryTreePanel(boolean open) {
        isCategoryPanelOpen = open;
        if (open) {
            view.getLeftSlidingMenuIcon().setResource(StyleResource.INSTANCE.images().toolbarButtonLeftToClose());
            animation.getLeftSlidingPanel().animate("left: -" + SLIDE_PX_CATEGORY, 0);
            animation.getLeftSlidingPanel().animate("left: +=" + SLIDE_PX_CATEGORY);
        } else {
            view.getLeftSlidingMenuIcon().setResource(StyleResource.INSTANCE.images().toolbarButtonLeftToOpen());
            animation.getLeftSlidingPanel().animate("left: -=" + SLIDE_PX_CATEGORY);
        }
    }

    /**
     * Animates right sliding panel.
     * @param open - true to slide right sliding panel to be visible, false otherwise
     */
    private void openDetailPanel(boolean open) {
        isDetailPanelOpen = open;
        if (open) {
            view.getRightSlidingMenuIcon().setResource(StyleResource.INSTANCE.images().toolbarButtonRightToClose());
            animation.getRightSlidingPanel().animate("right: -" + SLIDE_PX_DETAIL, 0);
            animation.getRightSlidingPanel().animate("right: +=" + SLIDE_PX_DETAIL);
        } else {
            view.getRightSlidingMenuIcon().setResource(StyleResource.INSTANCE.images().toolbarButtonRightToOpen());
            animation.getRightSlidingPanel().animate("right: -=" + SLIDE_PX_DETAIL);
        }
    }
}