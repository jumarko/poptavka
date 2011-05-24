package cz.poptavka.sample.domain.user;

import cz.poptavka.sample.domain.demand.Demand;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.List;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 * @author Excalibur
 * @author Juraj Martinka
 */
@Entity
//@org.hibernate.annotations.Table(comment = )
@Audited
public class Client extends BusinessUserRole {

    @OneToMany(mappedBy = "client")
    @Cascade(value = CascadeType.ALL)
    private List<Demand> demands;

    /** Total rating of supplier for all his "processed" demands .*/
    private Integer overalRating;

    @OneToOne(mappedBy = "client")
    @NotAudited
    private SupplierBlacklist supplierBlacklist;


    public List<Demand> getDemands() {
        return demands;
    }

    public void setDemands(List<Demand> demands) {
        this.demands = demands;
    }

    public Integer getOveralRating() {
        return overalRating;
    }

    public void setOveralRating(Integer overalRating) {
        this.overalRating = overalRating;
    }

    public SupplierBlacklist getSupplierBlacklist() {
        return supplierBlacklist;
    }

    public void setSupplierBlacklist(SupplierBlacklist supplierBlacklist) {
        this.supplierBlacklist = supplierBlacklist;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Client");
        sb.append(", overalRating=").append(overalRating);
        sb.append('}');
        return sb.toString();
    }
}
