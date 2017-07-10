package tr.com.lucidcode.pojo;

import java.util.Date;

/**
 * Created by adinema on 08/07/17.
 */
public class MoneyControlDataOutput {

    private Date date;
    private String scrip;
    private String reportType;
    private String key;
    private Float value;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getScrip() {
        return scrip;
    }

    public void setScrip(String scrip) {
        this.scrip = scrip;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String toString(){
        return scrip + " " + key + " " + value;
    }
}
