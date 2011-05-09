package cz.poptavka.sample.service.user;

import cz.poptavka.sample.base.integration.DBUnitBaseTest;
import cz.poptavka.sample.base.integration.DataSet;
import cz.poptavka.sample.domain.user.BusinessUser;
import cz.poptavka.sample.domain.user.BusinessUserRole;
import cz.poptavka.sample.domain.user.Client;
import cz.poptavka.sample.domain.user.Partner;
import cz.poptavka.sample.domain.user.Supplier;
import cz.poptavka.sample.service.GeneralService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Juraj Martinka
 *         Date: 9.5.11
 */
@DataSet(path = "classpath:cz/poptavka/sample/domain/user/UsersDataSet.xml", dtd = "classpath:test.dtd")
public class BusinessUserTest extends DBUnitBaseTest {

    @Autowired
    private GeneralService generalService;

    @Test
    public void testBusinessUserWithMultipleRoles() {
        final BusinessUser userWithMultipleRoles = this.generalService.find(BusinessUser.class, 111111114L);
        Assert.assertNotNull(userWithMultipleRoles);
        Assert.assertEquals(3, userWithMultipleRoles.getBusinessUserRoles().size());
        checkUserRole(Client.class, userWithMultipleRoles);
        checkUserRole(Supplier.class, userWithMultipleRoles);
        checkUserRole(Partner.class, userWithMultipleRoles);

        // for sure, check other user data
        Assert.assertEquals("My Fourth Company", userWithMultipleRoles.getBusinessUserData().getCompanyName());
        Assert.assertEquals("elviraM@email.com", userWithMultipleRoles.getEmail());
    }


    @Test
    public void testUserSettings() {

    }


    //---------------------------------------------- HELPER METHEODS ---------------------------------------------------
    /**
     * Checks if given user <code>businessUser</code> has role specified by
     * @param businessRoleClass class
     * @param businessUser
     */
    private void checkUserRole(final Class<? extends BusinessUserRole> businessRoleClass, BusinessUser businessUser) {
        Assert.assertNotNull(businessUser);
        Assert.assertNotNull(businessRoleClass);
        Assert.assertTrue("User does not have the role: " + businessRoleClass,
                CollectionUtils.exists(businessUser.getBusinessUserRoles(), new Predicate() {
                    @Override
                    public boolean evaluate(Object object) {
                        return businessRoleClass.equals(((BusinessUserRole) object).getClass());
                    }
                }));
    }
}
