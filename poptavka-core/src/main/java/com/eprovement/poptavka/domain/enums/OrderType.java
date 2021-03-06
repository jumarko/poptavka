package com.eprovement.poptavka.domain.enums;

/**
 * Represents ways of ordering results, namely:
 * <ul>
 *     <li>Ascending</li>
 *     <li>Descending</li>
 * </ul>
 *
 * @author Juraj Martinka
 *         Date: 30.4.11
 */
public enum OrderType {
    ASC("ascending"),
    DESC("descending");

    private final String value;

    OrderType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
