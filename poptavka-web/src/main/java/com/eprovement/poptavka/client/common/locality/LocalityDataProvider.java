package com.eprovement.poptavka.client.common.locality;

import com.eprovement.poptavka.client.common.security.SecuredAsyncCallback;
import com.eprovement.poptavka.client.service.demand.LocalityRPCServiceAsync;
import com.eprovement.poptavka.domain.enums.LocalityType;
import com.eprovement.poptavka.shared.domain.LocalityDetail;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import java.util.List;

public class LocalityDataProvider extends AsyncDataProvider<LocalityDetail> {

    private LocalityRPCServiceAsync localityService;
    private LocalityDetail localityDetail;

    public LocalityDataProvider(LocalityDetail locCode, LocalityRPCServiceAsync localityService) {
        this.localityDetail = locCode;
        this.localityService = localityService;
    }

    @Override
    protected void onRangeChanged(HasData<LocalityDetail> display) {
        if (localityDetail == null) {
            localityService.getLocalities(LocalityType.REGION, new SecuredAsyncCallback<List<LocalityDetail>>() {

                @Override
                public void onSuccess(List<LocalityDetail> result) {
                    updateRowCount(result.size(), true);
                    updateRowData(0, result);
                }
            });
        } else {
            localityService.getLocalities(localityDetail.getCode(), new SecuredAsyncCallback<List<LocalityDetail>>() {

                @Override
                public void onSuccess(List<LocalityDetail> result) {
                    updateRowCount(result.size(), true);
                    updateRowData(0, result);
                }
            });
        }
    }
}