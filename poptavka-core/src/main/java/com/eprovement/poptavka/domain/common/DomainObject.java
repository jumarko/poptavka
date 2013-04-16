package com.eprovement.poptavka.domain.common;

import org.apache.lucene.analysis.cz.CzechAnalyzer;
import org.hibernate.search.annotations.Analyzer;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * Base class for al domain objects.
 * Main responsibilities:
 * <ol>
 *     <li>Handles ID field mapping at one place.</li>
 *     <li>Defines "caseAccentInsensitiveAnalyzer" which enables case and accent insensitive fulltext search
 *         to be available on all domain objects. Particular object must use this Analyzer through annotation
 *        {@link org.hibernate.search.annotations.Analyzer}.
 *        See also {@link com.eprovement.poptavka.service.fulltext.HibernateFulltextSearchService}
 *     </li>
 * </ol>
 *
 */
@Analyzer(impl = CzechAnalyzer.class)
@MappedSuperclass
// filter all disabled objects - both '1' and NULL are considered as being enabled, object has to be disabled explicitly
@FilterDef(name = DomainObject.ENABLED_FILTER_NAME,
        defaultCondition = "(enabled = '1' OR enabled IS NULL)")
@Filter(name = DomainObject.ENABLED_FILTER_NAME)
public abstract class DomainObject implements Serializable {

    /** Id of the entity. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Determines whether this entity should be considered at all or not.
     * By default each entity is enabled unless explicitly made disabled.
     * Enabled flag is a less offensive substitution for deleting from database.
     *
     * This attribute has associated "columnDefinition" BIT which serves as a workaround, see:
     * <a href="http://stackoverflow.com/questions/8667965/found-bit-expected-boolean-after-hibernate-4-upgrade">
     *     found-bit-expected-boolean-after-hibernate-4-upgrade</a>
     */
    @Column(columnDefinition = "BIT")
    private Boolean enabled = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if (this.id != null && id == null) {
            throw new NullPointerException("id");
        }
        this.id = id;
    }

    /**
     * By default all domain objects should be enabled so this method returns true even if {@code enabled} is unset.
      * @return true if {@code enabled} flag is true or null, false otherwise
     */
    public Boolean isEnabled() {
        if (enabled == null) {
            // always consider unset "enabled" as true value
            return true;
        }
        return enabled;
    }

    /**
     * Sets whether this domain object is enabled or not.
     * @param enabled whether domain object should be enabled or not
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final DomainObject that = (DomainObject) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
