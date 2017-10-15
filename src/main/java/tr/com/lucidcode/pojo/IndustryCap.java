package tr.com.lucidcode.pojo;

/**
 * Created by adinema on 15/10/17.
 */
public class IndustryCap {

    String sector;
    Double year2011;
    Double year2012;
    Double year2013;
    Double year2014;
    Double year2015;
    Double year2016;
    Double year2017;

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public Double getYear2011() {
        return year2011;
    }

    public void setYear2011(Double year2011) {
        this.year2011 = year2011;
    }

    public Double getYear2012() {
        return year2012;
    }

    public void setYear2012(Double year2012) {
        this.year2012 = year2012;
    }

    public Double getYear2013() {
        return year2013;
    }

    public void setYear2013(Double year2013) {
        this.year2013 = year2013;
    }

    public Double getYear2014() {
        return year2014;
    }

    public void setYear2014(Double year2014) {
        this.year2014 = year2014;
    }

    public Double getYear2015() {
        return year2015;
    }

    public void setYear2015(Double year2015) {
        this.year2015 = year2015;
    }

    public Double getYear2016() {
        return year2016;
    }

    public void setYear2016(Double year2016) {
        this.year2016 = year2016;
    }

    public Double getYear2017() {
        return year2017;
    }

    public void setYear2017(Double year2017) {
        this.year2017 = year2017;
    }

    public String toString(){
        return sector + " - " + year2016 + " - " + year2015;
    }
}
