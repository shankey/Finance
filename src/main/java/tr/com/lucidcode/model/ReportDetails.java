package tr.com.lucidcode.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;


public class ReportDetails {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id;


    private Integer reportId;

    private String reportKey;
    private String reportValue;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public String getReportKey() {
        return reportKey;
    }

    public void setReportKey(String reportKey) {
        this.reportKey = reportKey;
    }

    public String getReportValue() {
        return reportValue;
    }

    public void setReportValue(String reportValue) {
        this.reportValue = reportValue;
    }

    @Override
    public String toString(){
        return reportKey + " " + reportValue ;
    }
}
