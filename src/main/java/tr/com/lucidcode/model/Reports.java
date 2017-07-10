package tr.com.lucidcode.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.Date;


/**
 * Created by adinema on 11/06/17.
 */
public class Reports {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id;


    private String bseId;

    private String reportType;
    private Date reportDate;
    private Integer source;
    private String nseId;
    private String moneyControlId;


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

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public String getNseId() {
        return nseId;
    }

    public void setNseId(String nseId) {
        this.nseId = nseId;
    }

    public String getMoneyControlId() {
        return moneyControlId;
    }

    public void setMoneyControlId(String moneyControlId) {
        this.moneyControlId = moneyControlId;
    }

    public String toString(){
        return source + " " + moneyControlId;
    }
}




