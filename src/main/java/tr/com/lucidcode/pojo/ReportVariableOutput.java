package tr.com.lucidcode.pojo;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by adinema on 18/06/17.
 */
public class ReportVariableOutput {

    Integer reportId;
    String reportType;
    Integer bseId;
    Date reportDate;
    String reportKeyMapping;
    Float reportValue;

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public Integer getBseId() {
        return bseId;
    }

    public void setBseId(Integer bseId) {
        this.bseId = bseId;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public String getReportKeyMapping() {
        return reportKeyMapping;
    }

    public void setReportKeyMapping(String reportKeyMapping) {
        this.reportKeyMapping = reportKeyMapping;
    }

    public Float getReportValue() {
        return reportValue;
    }

    public void setReportValue(Float reportValue) {
        this.reportValue = reportValue;
    }

    @Override
    public String toString() {
        return "ReportVariableOutput{" +
                "reportType='" + reportType + '\'' +
                ", bseId=" + bseId +
                ", reportDate=" + reportDate +
                ", reportKeyMapping='" + reportKeyMapping + '\'' +
                ", reportValue='" + reportValue + '\'' +
                '}';
    }
}

