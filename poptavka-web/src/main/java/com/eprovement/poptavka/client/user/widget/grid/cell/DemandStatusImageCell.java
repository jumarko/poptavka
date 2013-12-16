package com.eprovement.poptavka.client.user.widget.grid.cell;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.ImageResourceRenderer;
import com.google.gwt.user.client.ui.PopupPanel;

import com.eprovement.poptavka.client.common.session.Storage;
import com.eprovement.poptavka.domain.enums.DemandStatus;
import com.eprovement.poptavka.resources.StyleResource;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * NEW
 * * There are two meanings for this state. Brand new Client created Demand during registration
 * * and he must confirm email activation link. The other meaning is when Demand came from external system
 * * and we are waiting for approval to show Demand from non-registered Client.
 * * Until we receive link confirmation/approval this Demand is in state TEMPORARY.

 * TEMPORARY
 * * Registered/non-registered Client confirmed/approved TEMPORARY Demand.
 * * Operator must check this Demand and switch it to another state.
 *
 * TO_BE_CHECKED
 * * Operator checked the Demand which needs to be changed. Either some information is missing or it is a spam.
 *
 * INVALID
 * * Demand is properly described by Client and Operator switched it to ACTIVE state.
 *
 * ACTIVE
 * * No supplier were chosen for this demand and the validity of the Demand has expired.
 * * This Demand can be re-activated by Client.
 * * After Client re-activates the Demand it will go to the state of TO_BE_CHECKED.
 *
 * INACTIVE
 * * A Supplier is assigned to this Demand and is working on it.
 *
 * ASSIGNED
 * * A Supplier is assigned to this Demand and is working on it.
 *
 * FINNISHED
 * * A Supplier finished the realization of Demand and switched it to state FINISHED.
 *
 * CLOSED
 * * A Client checked FINISHED Demand and closed Demand if it was Ok.
 * * Otherwise Client switches back to ASSIGNED and Supplier has to rework it.
 *
 * CANCELED
 * * A Client or Operator canceled Demand on which the work could being done
 * * or the work has never stared for some reason.
 *
 * Clickable cell displaying star status of message.
 * Not all States are supported yet.
 *
 * @author beho
 * @param <C>
 *
 */
public class DemandStatusImageCell extends AbstractCell<DemandStatus> {

    private static ImageResourceRenderer renderer;
    private PopupPanel popup = new PopupPanel(true);
    private boolean displayed;

    public DemandStatusImageCell() {
        super("mouseover", "mouseout");
        if (renderer == null) {
            renderer = new ImageResourceRenderer();
        }
    }

    @Override
    public void render(com.google.gwt.cell.client.Cell.Context context,
            DemandStatus value, SafeHtmlBuilder sb) {

        if (value == null) {
            return;
        }

        setImage(value, sb);
    }

    @Override
    public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context,
            Element parent, DemandStatus value, NativeEvent event,
            ValueUpdater<DemandStatus> valueUpdater) {
        if ("mouseover".equals(event.getType())) {
            displayPopup(event, value);
        }
        if ("mouseout".equals(event.getType())) {
            hidePopup();
        }
    }

    private void displayPopup(NativeEvent event, DemandStatus demandStauts) {
        if (displayed) {
            return;
        }
        displayed = true;

        popup.clear();
        HTMLPanel htmlPanel2 = new HTMLPanel(getExplanationText(demandStauts));
        htmlPanel2.addStyleName("container-fluid");
        htmlPanel2.addStyleName("short-message");
        HTMLPanel htmlPanel1 = new HTMLPanel("");
        //popup.getElement().setInnerText(getExplanationText(demandStauts));
        htmlPanel1.add(htmlPanel2);
        htmlPanel1.setStylePrimaryName(StyleResource.INSTANCE.modal().commonModalStyle());
        popup.add(htmlPanel1);
        popup.setPopupPosition(event.getClientX() + 32, event.getClientY() + 32);
        popup.show();
    }

    private void hidePopup() {
        displayed = false;
        popup.hide();
    }

    private void setImage(DemandStatus value, SafeHtmlBuilder sb) {
        switch (value) {
            case NEW:
                sb.append(renderer.render(Storage.RSCS.images().newDemand()));
                break;
            case ACTIVE:
                sb.append(renderer.render(Storage.RSCS.images().acceptIcon16()));
                break;
            case OFFERED:
                sb.append(renderer.render(Storage.RSCS.images().offeredDemand()));
                break;
            default:
                break;
        }
    }

    private String getExplanationText(DemandStatus value) {
        switch (value) {
            case NEW:
                return Storage.MSGS.demandStatusNew();
            case ACTIVE:
                return Storage.MSGS.demandStatusActive();
            case OFFERED:
                return Storage.MSGS.demandStatusOffered();
            default:
                return "";
        }
    }
}
