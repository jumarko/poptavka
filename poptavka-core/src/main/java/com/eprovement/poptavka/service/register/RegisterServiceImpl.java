package com.eprovement.poptavka.service.register;

import static org.apache.commons.lang.Validate.notEmpty;

import com.google.common.base.Preconditions;
import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.genericdao.search.Search;
import com.eprovement.poptavka.service.GeneralService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * This class has no explicit test right now. However, so test must work with concrete register classes
 * (such as {@link com.eprovement.poptavka.domain.demand.DemandType}) therefore the satisfying test case for this
 * should be e.g. <code>DemandServiceIntegrationTest</code>.
 *
 * @see RegisterService
 *
 * @author Juraj Martinka
 *         Date: 17.5.11
 */
public class RegisterServiceImpl implements RegisterService {

    private GeneralService generalService;

    public RegisterServiceImpl(GeneralService generalService) {
        Preconditions.checkArgument(generalService != null);
        this.generalService = generalService;
    }


    /** {@inheritDoc} */
    @Override
    @Cacheable(cacheName = "cache5h")
    @Transactional(readOnly = true)
    public <T> List<T> getAllValues(Class<T> registerClass) {
        return this.generalService.findAll(registerClass);
    }

    /** {@inheritDoc} */
    @Override
    @Cacheable(cacheName = "cache5h")
    @Transactional(readOnly = true)
    public <T> T getValue(String code, Class<T> registerClass) {
        notEmpty(code, "code cannot be empty!");
        return (T) generalService.searchUnique(new Search(registerClass)
                .addFilterEqual("code", code));
    }
}
