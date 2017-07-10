package tr.com.lucidcode.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


public class ReportKeyMappings {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id;


    private String reportKey;
    private String reportKeyMapping;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReportKey() {
        return reportKey;
    }

    public void setReportKey(String reportKey) {
        this.reportKey = reportKey;
    }

    public String getReportKeyMapping() {
        return reportKeyMapping;
    }

    public void setReportKeyMapping(String reportKeyMapping) {
        this.reportKeyMapping = reportKeyMapping;
    }

    @Override
    public String toString(){
        return reportKey + " " + reportKeyMapping;
    }
}
