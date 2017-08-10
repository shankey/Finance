package tr.com.lucidcode.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by adinema on 04/08/17.
 */
public class MutualFundsPerformance {

    @Id
    @GeneratedValue
    @Column(name = "id")
    Integer id;

    Integer mutualFundId;
    Double nav;
    Double repurchasePrice;
    Double salePrice;
    Date date;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMutualFundId() {
        return mutualFundId;
    }

    public void setMutualFundId(Integer mutualFundId) {
        this.mutualFundId = mutualFundId;
    }

    public Double getNav() {
        return nav;
    }

    public void setNav(Double nav) {
        this.nav = nav;
    }

    public Double getRepurchasePrice() {
        return repurchasePrice;
    }

    public void setRepurchasePrice(Double repurchasePrice) {
        this.repurchasePrice = repurchasePrice;
    }

    public Double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Double salePrice) {
        this.salePrice = salePrice;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "MutualFundsPerformance{" +
                "mutualFundId=" + mutualFundId +
                ", nav=" + nav +
                ", date=" + date +
                '}';
    }
}
