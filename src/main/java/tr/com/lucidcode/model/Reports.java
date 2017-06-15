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

    @NotNull
    private String bseId;

    private String reportType;
    private Date reportDate;


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
}


