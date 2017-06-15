package tr.com.lucidcode.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


public class StockSymbols {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id;

    @NotNull
    private String bseId;

    private String scrip;
    private String industry;
    private String company;
    private Float status;
    private Integer priceStatus;

    public Float getStatus() {
        return status;
    }

    public void setStatus(Float status) {
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBseId() {
        return bseId;
    }

    public void setBseId(String bseId) {
        this.bseId = bseId;
    }

    public String getScrip() {
        return scrip;
    }

    public void setScrip(String scrip) {
        this.scrip = scrip;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Integer getPriceStatus() {
        return priceStatus;
    }

    public void setPriceStatus(Integer priceStatus) {
        this.priceStatus = priceStatus;
    }

    @Override
    public String toString(){
        return scrip + " " + company + " " + industry;
    }
}
