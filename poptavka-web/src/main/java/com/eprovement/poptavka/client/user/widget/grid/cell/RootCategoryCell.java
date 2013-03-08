package com.eprovement.poptavka.client.user.widget.grid.cell;

import com.eprovement.poptavka.shared.domain.CategoryDetail;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiRenderer;

public class RootCategoryCell extends AbstractCell<CategoryDetail> {

    /**************************************************************************/
    /* UiRenderer                                                             */
    /**************************************************************************/
    private static RootCategoryCell.MyUiRenderer renderer = GWT.create(RootCategoryCell.MyUiRenderer.class);

    interface MyUiRenderer extends UiRenderer {

        void render(SafeHtmlBuilder sb, String catName, String catCount);
    }

    /**************************************************************************/
    /* Overriden methods                                                      */
    /**************************************************************************/
    @Override
    public void render(Cell.Context context, CategoryDetail value, SafeHtmlBuilder sb) {
        if (value != null) {
            renderer.render(sb,
                    value.getName().replaceAll("-a-", " a ").replaceAll("-", ", "),
                    String.valueOf(value.getDemandsCount()));
        }
    }
}