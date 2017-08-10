package tr.com.lucidcode.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by adinema on 04/08/17.
 */
public class MutualFunds {

    @Id
    @GeneratedValue
    @Column(name = "id")
    Integer id;

    String schemeType;
    String fundHouse;
    Integer schemeCode;
    String isin;
    String schemeName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSchemeType() {
        return schemeType;
    }

    public void setSchemeType(String schemeType) {
        this.schemeType = schemeType;
    }

    public String getFundHouse() {
        return fundHouse;
    }

    public void setFundHouse(String fundHouse) {
        this.fundHouse = fundHouse;
    }

    public Integer getSchemeCode() {
        return schemeCode;
    }

    public void setSchemeCode(Integer schemeCode) {
        this.schemeCode = schemeCode;
    }

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    @Override
    public String toString() {
        return "MutualFunds{" +
                "schemeType='" + schemeType + '\'' +
                ", fundHouse='" + fundHouse + '\'' +
                ", schemeCode=" + schemeCode +
                ", schemeName='" + schemeName + '\'' +
                '}';
    }
}
