package com.eprovement.poptavka.dao.message;

import com.eprovement.poptavka.dao.GenericHibernateDao;
import com.eprovement.poptavka.domain.demand.Demand;
import com.eprovement.poptavka.domain.message.Message;
import com.eprovement.poptavka.domain.message.MessageUserRole;
import com.eprovement.poptavka.domain.enums.MessageUserRoleType;
import com.eprovement.poptavka.domain.message.UserMessage;
import com.eprovement.poptavka.domain.user.User;
import java.util.ArrayList;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Juraj Martinka
 *         Date: 4.5.11
 */
public class MessageDaoImpl extends GenericHibernateDao<Message> implements MessageDao {

    @Override
    public List<Message> getMessageThreads(User user, MessageFilter messageFilter) {
        final Criteria userMessagesCriteria = buildUserMessagesCriteria(user, messageFilter);

        // only messages without parent are considered to be thread roots
        userMessagesCriteria.createCriteria("message").add(Restrictions.isNull("parent"));

        return buildResultCriteria(userMessagesCriteria, messageFilter.getResultCriteria()).list();
    }

    @Override
    public List<Message> getAllMessages(User user, MessageFilter messageFilter) {
        if (messageFilter == null) {
            messageFilter = MessageFilter.EMPTY_FILTER;
        }

        return buildResultCriteria(buildUserMessagesCriteria(user, messageFilter),
                messageFilter.getResultCriteria()).list();
    }

    @Override
    public List<UserMessage> getUserMessages(List<Message> messages, MessageFilter messageFilter) {
        final Criteria userMessageCriteria = buildUserMessageCriteria(messages, messageFilter);
        return buildResultCriteria(userMessageCriteria, messageFilter.getResultCriteria()).list();
    }

    /**
     * Loads conversation between supplier and  client related to potential demand supplier's queries.
     *
     * Juraj tato metoda mi musi vratit vsetky spravy medzi Dodavatelom(parameter User user) a
     * Klientom (ziskas z parametru message.getSender()). Vsetky tieto spravy maju spolocne
     * vlakno messageThread (vstupny parameter message). Snazil som sa tento SQL zostavit cez
     * Criteria ale ako vidis nizsie nepodarilo sa mi to. Chcel som si vyselektovat vsetky
     * spravy s danym threadRoot a s danymi rolami. Tieto role my mali vymedzit 2 typy
     * sprav (
     * 1. vsetky spravy od Dodavatela(SENDER) ku Klientovi(TO) a
     * 2. vsetky spravy od Klienta(SENDER) ku Dodavatelovi(TO). Tymto by som mal ziskat
     * uplne vsetky spravy tohto vlakna obmedzene len na daneho dodavatela a klienta.
     *
     * PS: Tieto role si vytvorim ako objetky ale je treba ich nacitat priamo z DB,
     * aby konecne vytvorenie selectu nehadzalo chybu parameter je null.  ROle si z DB
     * nenacitavam pretoze neviem ako a cela tato metoda sa mi nepaci. Potrebujem tvoju
     * expertizu.
     *
     * @param threadRoot
     * @param supplierUser
     * @return
     */
    @Override
    public List<Message> getPotentialDemandConversation(Message threadRoot, User supplierUser) {
        final HashMap<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("threadRoot", threadRoot);
        queryParams.put("supplier", supplierUser);

        return runNamedQuery("getPotentialDemandConversation", queryParams);
    }

    /**
     * Loads conversation between supplier and  client related to potential demand supplier's queries.
     *
     * @param threadRoot
     * @param supplierUser
     * @return list of user messages.
     */
    @Override
    public List<UserMessage> getConversationUserMessages(Message threadRoot, User user) {
        final HashMap<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("threadRoot", threadRoot);
        queryParams.put("user", user);

        return runNamedQuery("getConversationUserMessages", queryParams);
    }

    @Override
    public List<Message> getPotentialOfferConversation(Message threadRoot, User supplierUser) {
        final HashMap<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("threadRoot", threadRoot);
        queryParams.put("supplier", supplierUser);

        return runNamedQuery("getPotentialOfferConversation", queryParams);
    }

    @Override
    public Message getThreadRootMessage(Demand demand) {
        final Criteria criteria = getHibernateSession().createCriteria(Message.class);
        criteria.add(Restrictions.eq("demand", demand)).add(Restrictions.isNull("parent"));
        return (Message) criteria.uniqueResult();
    }

    @Override
    public List<Message> getAllDescendants(List<Message> messages) {
        List<Message> result = new ArrayList();
        for (Message message : messages) {
            List<Message> children = message.getChildren();
            result.addAll(children);
            result.addAll(getAllDescendants(children));
        }
        return result;
    }

    @Override
    public Message getLastChild(Message parent) {
        final HashMap<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("parent", parent);

        List<Message> messages = runNamedQuery("getLastChild", queryParams);
        if (messages.isEmpty()) {
            return null;
        } else {
            return messages.get(0);
        }
    }


    //---------------------------------------------- HELPER METHODS ---------------------------------------------------
    /**
     * Build criterion which can be used for getting all messages of given user restricted by
     * <code>messageFilter</code>.
     *
     * @param user
     * @param messageFilter
     * @return
     */
    private Criteria buildUserMessagesCriteria(User user, MessageFilter messageFilter) {
        final Criteria userMessagesCriteria;
        if (messageFilter != null
                && messageFilter.getMessageUserRoleType() != null
                && messageFilter.getMessageUserRoleType() != MessageUserRoleType.SENDER) {
            userMessagesCriteria = getHibernateSession().createCriteria(MessageUserRole.class);
            userMessagesCriteria.add(Restrictions.eq("user", user));
            userMessagesCriteria.setProjection(Projections.property("message"));
            userMessagesCriteria.createCriteria("message").createCriteria("roles")
                    .add(Restrictions.eq("user", user))
                    .add(Restrictions.eq("type", messageFilter.getMessageUserRoleType()));
        } else {
            userMessagesCriteria = getHibernateSession().createCriteria(UserMessage.class);
            userMessagesCriteria.add(Restrictions.eq("user", user));
            userMessagesCriteria.setProjection(Projections.property("message"));
            if (messageFilter != null
                    && messageFilter.getMessageUserRoleType() != null
                    && messageFilter.getMessageUserRoleType() == MessageUserRoleType.SENDER) {
                userMessagesCriteria.createAlias("message", "msg");
                userMessagesCriteria.add(Restrictions.eq("msg.sender", user));
            }
        }
        return userMessagesCriteria;
    }

    /**
     * Build criterion which can be used for getting a userMessage of given message.
     *
     * @param messages
     * @param messageFilter
     * @return
     */
    private Criteria buildUserMessageCriteria(List<Message> messages, MessageFilter messageFilter) {
        final Criteria userMessageCriteria = getHibernateSession().createCriteria(UserMessage.class);
//        userMessageCriteria.add(Restrictions.eq("message", messages));
        userMessageCriteria.add(Restrictions.in("message", messages));
//        userMessageCriteria.add(Restrictions.eq("user", this));
//        userMessageCriteria.setProjection(Projections.property("message"));
        return userMessageCriteria;
    }

    public Map<Message, Long> getListOfClientDemandMessagesHelper(User user,
            String queryName) {
        final HashMap<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("sender", user);
        queryParams.put("user", user);

        List<Object[]> unread = runNamedQuery(
                queryName,
                queryParams);
        Map<Long, Long> unreadMap = new HashMap();
        for (Object[] entry : unread) {
            unreadMap.put((Long) entry[0], (Long) entry[1]);
        }
        Map<Message, Long> messageMap = new HashMap();
        queryParams.remove("user");
        List<Message> messages = runNamedQuery(
                "getListOfClientDemandMessages",
                queryParams);
        for (Message message : messages) {
            if (unreadMap.containsKey(message.getId())) {
                messageMap.put(message, unreadMap.get(message.getId()));
            } else {
                messageMap.put(message, 0L);
            }
        }
        return messageMap;
    }

    private Map<Message, Integer> longValueMapToIntValueMap(
            Map<Message, Long> longMap) {
        Map<Message, Integer> result = new HashMap();
        for (Map.Entry<Message, Long> entry : longMap.entrySet()) {
            result.put(entry.getKey(), (int) entry.getValue().intValue());
        }
        return result;

    }

}
