/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eprovement.poptavka.dao.usermessage;

import com.eprovement.poptavka.dao.GenericHibernateDao;
import com.eprovement.poptavka.dao.message.MessageFilter;
import com.eprovement.poptavka.domain.message.Message;
import com.eprovement.poptavka.domain.message.UserMessage;
import com.eprovement.poptavka.domain.user.BusinessUser;
import com.eprovement.poptavka.domain.user.User;
import java.util.HashMap;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 *
 * @author ivan.vlcek
 */
public class UserMessageDaoImpl extends GenericHibernateDao<UserMessage> implements UserMessageDao {

    // TODO ivlcek - maybe I will have to replace MessageFilter by UserMessageFiletr
    @Override
    public List<UserMessage> getUserMessages(List<Message> messages, User user, MessageFilter messageFilter) {
        final Criteria userMessageCriteria = buildUserMessageCriteria(messages, user, messageFilter);
        // TODO ivlcek - remove this method. It will be in a separate
        return buildResultCriteria(userMessageCriteria, messageFilter.getResultCriteria()).list();
    }

    @Override
    public UserMessage getUserMessage(Message message, User user) {
        final HashMap<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("user", user);
        queryParams.put("message", message);

        List<UserMessage> userMessages = runNamedQuery("getUserMesage", queryParams);
        if (userMessages.isEmpty()) {
            return null;
        } else {
            return userMessages.get(0);
        }
    }

    @Override
    public List<UserMessage> getInbox(User user) {
        final HashMap<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("user", user);

        return runNamedQuery("getInbox", queryParams);
    }

    @Override
    public List<UserMessage> getSentItems(User user) {
        final HashMap<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("user", user);

        return runNamedQuery("getSentItems", queryParams);
    }

    @Override
    public List<UserMessage> getPotentialDemands(BusinessUser supplier) {
        final HashMap<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("supplier", supplier);

        return runNamedQuery("getPotentialDemands", queryParams);
    }


    //---------------------------------------------- HELPER METHODS ---------------------------------------------------
    /**
     * Build criterion which can be used for getting a userMessage of given message.
     *
     * @param message
     * @param messageFilter
     * @return
     */
    private Criteria buildUserMessageCriteria(List<Message> messages, User user, MessageFilter messageFilter) {
        final Criteria userMessageCriteria = getHibernateSession().createCriteria(UserMessage.class);
//        userMessageCriteria.add(Restrictions.eq("message", messages));
        userMessageCriteria.add(Restrictions.eq("user", user));
        if (CollectionUtils.isNotEmpty(messages)) {
            userMessageCriteria.add(Restrictions.in("message", messages));
        }


//        userMessageCriteria.setProjection(Projections.property("message"));
        return userMessageCriteria;
    }
}