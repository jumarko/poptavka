package com.eprovement.poptavka.domain.demand;

import com.eprovement.poptavka.domain.common.DomainObject;

import javax.persistence.Entity;

/**
 * Very simple template containing only one (potentially long) string
 * which is referred as a <code>prefilledInputs</code>
 * <p>
 * This template can be used for various purposes.
 *
 * @see Category
 *
 * @author Juraj Martinka
 *         Date: 10.4.11
 */
@Entity
public class Template extends DomainObject {

    // TODO LATER juraj: "co chtel basnik rici? - vid Dat. model"
    private String prefilledInputs;

    public String getPrefilledInputs() {
        return prefilledInputs;
    }

    public void setPrefilledInputs(String prefilledInputs) {
        this.prefilledInputs = prefilledInputs;
    }



    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Template");
        sb.append("{prefilledInputs='").append(prefilledInputs).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
