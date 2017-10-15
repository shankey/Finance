package tr.com.lucidcode.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;


/**
 * Created by adinema on 11/06/17.
 */
public class RatioDetails {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id;


    private String moneyControlId;
    private String ratio;
    private String reportType;
    private String sector;

    private Float year2011;
    private Float year2012;
    private Float year2013;
    private Float year2014;
    private Float year2015;
    private Float year2016;
    private Float year2017;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMoneyControlId() {
        return moneyControlId;
    }

    public void setMoneyControlId(String moneyControlId) {
        this.moneyControlId = moneyControlId;
    }

    public String getRatio() {
        return ratio;
    }

    public void setRatio(String ratio) {
        this.ratio = ratio;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public Float getYear2011() {
        return year2011;
    }

    public void setYear2011(Float year2011) {
        this.year2011 = year2011;
    }

    public Float getYear2012() {
        return year2012;
    }

    public void setYear2012(Float year2012) {
        this.year2012 = year2012;
    }

    public Float getYear2013() {
        return year2013;
    }

    public void setYear2013(Float year2013) {
        this.year2013 = year2013;
    }

    public Float getYear2014() {
        return year2014;
    }

    public void setYear2014(Float year2014) {
        this.year2014 = year2014;
    }

    public Float getYear2015() {
        return year2015;
    }

    public void setYear2015(Float year2015) {
        this.year2015 = year2015;
    }

    public Float getYear2016() {
        return year2016;
    }

    public void setYear2016(Float year2016) {
        this.year2016 = year2016;
    }

    public Float getYear2017() {
        return year2017;
    }

    public void setYear2017(Float year2017) {
        this.year2017 = year2017;
    }

    public String toString(){
        return moneyControlId + " - " + ratio + " - " + year2016;
    }
}




