/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.poptavka.sample.shared.domain;

import java.io.Serializable;
import java.util.Date;

import cz.poptavka.sample.domain.message.Message;

/**
 *
 * @author ivan.vlcek
 */
public class MessageDetail implements Serializable {

    /**
     * Generated serialVersionUID.
     */
    private static final long serialVersionUID = -928374659233195109L;
    private long id;
    private long threadRootId;
    private long parentId;
    private long firstBornId;
    private long nexSiblingId;
    private long demandId;
    private String subject;
    private String body;
    private String messageState;
    private Date created;
    private Date sent;
    private long senderId;
    private long receiverId;

    public static MessageDetail generateMessageDetail(Message message) {
        MessageDetail m = new MessageDetail();
        m.setId(message.getId());
        m.setBody(message.getBody());
        m.setCreated(message.getCreated());
//        m.setFirstBornId(serialVersionUID);
        m.setMessageState(message.getMessageState().name());
//        m.setNexSiblingId(serialVersionUID);
        m.setParentId(message.getParent() == null ? m.getThreadRootId() : message.getParent().getId());
//        m.setReceiverId();
        m.setSenderId(message.getSender().getId());
        m.setSent(message.getSent());
        m.setSubject(message.getSubject());
        m.setThreadRootId(message.getThreadRoot().getId());
        return m;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    /**
     * @return the threadRootId
     */
    public long getThreadRootId() {
        return threadRootId;
    }

    /**
     * @param threadRootId the threadRootId to set
     */
    public void setThreadRootId(long threadRootId) {
        this.threadRootId = threadRootId;
    }

    /**
     * @return the parentId
     */
    public long getParentId() {
        return parentId;
    }

    /**
     * @param parentId the parentId to set
     */
    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    /**
     * @return the firstBornId
     */
    public long getFirstBornId() {
        return firstBornId;
    }

    /**
     * @param firstBornId the firstBornId to set
     */
    public void setFirstBornId(long firstBornId) {
        this.firstBornId = firstBornId;
    }

    /**
     * @return the nexSiblingId
     */
    public long getNexSiblingId() {
        return nexSiblingId;
    }

    /**
     * @param nexSiblingId the nexSiblingId to set
     */
    public void setNexSiblingId(long nexSiblingId) {
        this.nexSiblingId = nexSiblingId;
    }

    public long getDemandId() {
        return demandId;
    }

    public void setDemandId(long demandId) {
        this.demandId = demandId;
    }

    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject the subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @return the body
     */
    public String getBody() {
        return body;
    }

    /**
     * @param body the body to set
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * @return the messageState
     */
    public String getMessageState() {
        return messageState;
    }

    /**
     * @param messageState the messageState to set
     */
    public void setMessageState(String messageState) {
        this.messageState = messageState;
    }

    /**
     * @return the created
     */
    public Date getCreated() {
        return created;
    }

    /**
     * @param created the created to set
     */
    public void setCreated(Date created) {
        this.created = created;
    }

    /**
     * @return the sent
     */
    public Date getSent() {
        return sent;
    }

    /**
     * @param sent the sent to set
     */
    public void setSent(Date sent) {
        this.sent = sent;
    }

    /**
     * @return the senderId
     */
    public long getSenderId() {
        return senderId;
    }

    /**
     * @param senderId the senderId to set
     */
    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    /**
     * @return the receiverId
     */
    public long getReceiverId() {
        return receiverId;
    }

    /**
     * @param receiverId the receiverId to set
     */
    public void setReceiverId(long receiverId) {
        this.receiverId = receiverId;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Message ID: " + id);
        sb.append("\nThreadID: " + threadRootId);
        sb.append("\nParentID: " + parentId);
        sb.append("\nFirstBornIdID: " + firstBornId);
        sb.append("\nNexSiblingID: " + nexSiblingId);
        sb.append("\ndemandID: " + demandId);
        sb.append("\nSubject: " + subject);
        sb.append("\nBody: " + body);
        sb.append("\nMessageState: " + messageState);
        sb.append("\nDate-Created: " + (created == null ? "null" : created.toString()));
        sb.append("\nDate-sent: " + (sent == null ? "null" : sent.toString()));
        sb.append("\nSenderID: " + senderId);
        sb.append("\nReceiverID: " + receiverId);
        return sb.toString();
    }

}
