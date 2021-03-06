package com.eprovement.poptavka.domain.register;

public final class Registers {



    public static enum Notification {

        NEW_MESSAGE("new.message"),
        NEW_INFO ("new.info"),
        NEW_MESSAGE_OPERATOR("new.message.operator"),

        NEW_DEMAND("new.demand"),
        OFFER_STATUS_CHANGED("offer.status.changed"),
        NEW_OFFER("new.offer"),
        DEMAND_STATUS_CHANGED("demand.status.changed"),

        WELCOME_CLIENT("welcome.client"),
        WELCOME_SUPPLIER("welcome.supplier"),

        /** Notification send to (unverified) external client when he gets new offer from supplier */
        EXTERNAL_CLIENT("external.client"),
        /** Notification send to (unverified) external supplier when he gets new potential demand. */
        EXTERNAL_SUPPLIER("external.supplier");

        private final String code;

        private Notification(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }



    public static final class Service {
        /* CLASSIC service is intended for Client role only. */
        public static final String CLASSIC = "CLASSIC";
        /* FREE, BASIC, PROFI services are intended for Supplier. */
        public static final String FREE = "FREE";
        public static final String BASIC = "BASIC";
        public static final String PROFI = "PROFI";
    }

    /**
     * This method exists only for satisfaction of DomainObjectTest. No real meaning.
     * @return empty string
     */
    public String toString() {
        return "";
    }
}
