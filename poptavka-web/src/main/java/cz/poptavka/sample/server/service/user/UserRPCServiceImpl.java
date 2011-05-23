package cz.poptavka.sample.server.service.user;

import com.googlecode.genericdao.search.Search;
import cz.poptavka.sample.client.service.demand.UserRPCService;
import cz.poptavka.sample.domain.user.User;
import cz.poptavka.sample.server.service.AutoinjectingRemoteService;
import cz.poptavka.sample.service.GeneralService;
import cz.poptavka.sample.shared.domain.UserDetail;
import org.springframework.beans.factory.annotation.Autowired;

public class UserRPCServiceImpl extends AutoinjectingRemoteService implements UserRPCService {

    private static final long serialVersionUID = 1132667081084321575L;

    private GeneralService generalService;

    @Autowired
    public void setGeneralService(GeneralService generalService) {
        this.generalService = generalService;
    }

    @Override
    public UserDetail loginUser(UserDetail userDetail) {

        final User user = (User) generalService.searchUnique(
                new Search(User.class).addFilterEqual("email", userDetail.getEmail())
                    .addFilterEqual("password", userDetail.getPassword()));
        if (user == null) {
            return null;
        }
        userDetail.setEmail(user.getEmail());
        userDetail.setPassword(user.getPassword());
        return userDetail;
    }
}
