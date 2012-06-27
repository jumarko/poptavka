package com.eprovement.poptavka.client.service.demand;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import com.eprovement.poptavka.shared.domain.UserDetail;
import com.eprovement.poptavka.shared.domain.BusinessUserDetail;
import com.eprovement.poptavka.shared.exceptions.RPCException;

@RemoteServiceRelativePath(UserRPCService.URL)
public interface UserRPCService extends RemoteService {

    String URL = "service/user";

    UserDetail loginUser(BusinessUserDetail user) throws RPCException;

    BusinessUserDetail getSignedUser(String sessionId) throws RPCException;

    BusinessUserDetail getUserById(Long userId) throws RPCException;

    /**
     * Checks wheter given {@code email} is available.
     * @param email ęmail address to be checked
     */
    boolean checkFreeEmail(String email) throws RPCException;
}